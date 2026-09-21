package com.Kalakriti.Kalakriti.service;

import com.Kalakriti.Kalakriti.entity.Order;
import com.Kalakriti.Kalakriti.entity.OrderItem;
import com.Kalakriti.Kalakriti.entity.OrderStatus;
import com.Kalakriti.Kalakriti.entity.User;
import com.Kalakriti.Kalakriti.repository.OrderRepository;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class InvoiceService {

    private final OrderRepository orderRepository;

    public InvoiceService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional(readOnly = true)
    public byte[] generateInvoice(Long orderId, User user)
            throws DocumentException {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new RuntimeException("Order not found")
                );

        if (order.getUser() == null
                || !order.getUser().getId().equals(user.getId())) {

            throw new RuntimeException(
                    "You are not authorized to download this invoice"
            );
        }

        if (order.getPaymentStatus() == null
                || !order.getPaymentStatus().name().equals("PAID")) {

            throw new RuntimeException(
                    "Invoice is available only for paid orders"
            );
        }

        ByteArrayOutputStream outputStream =
                new ByteArrayOutputStream();

        Document document = new Document();

        PdfWriter.getInstance(document, outputStream);

        document.open();

        Font titleFont = FontFactory.getFont(
                FontFactory.HELVETICA_BOLD,
                22
        );

        Font headingFont = FontFactory.getFont(
                FontFactory.HELVETICA_BOLD,
                12
        );

        Font normalFont = FontFactory.getFont(
                FontFactory.HELVETICA,
                10
        );

        Font cancelledFont = FontFactory.getFont(
                FontFactory.HELVETICA_BOLD,
                14
        );

        Paragraph title = new Paragraph(
                "KALAKRITI",
                titleFont
        );
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        Paragraph invoiceTitle = new Paragraph(
                "INVOICE",
                headingFont
        );
        invoiceTitle.setAlignment(Element.ALIGN_CENTER);
        document.add(invoiceTitle);

        document.add(new Paragraph(" "));

        /*
         * Display a clear cancellation message.
         * The invoice is still available because the payment
         * was already completed.
         */
        if (order.getStatus() == OrderStatus.CANCELLED) {

            Paragraph cancelledTitle = new Paragraph(
                    "ORDER CANCELLED",
                    cancelledFont
            );

            cancelledTitle.setAlignment(Element.ALIGN_CENTER);
            document.add(cancelledTitle);

            Paragraph refundStatus = new Paragraph(
                    "Refund Status: REFUND PENDING",
                    headingFont
            );

            refundStatus.setAlignment(Element.ALIGN_CENTER);
            document.add(refundStatus);

            document.add(new Paragraph(
                    "This order was cancelled after payment. "
                            + "The refund will be processed separately.",
                    normalFont
            ));

            document.add(new Paragraph(" "));
        }

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern(
                        "dd MMM yyyy, hh:mm a"
                );

        String orderDate = order.getCreatedAt() == null
                ? "N/A"
                : order.getCreatedAt().format(formatter);

        document.add(new Paragraph(
                "Invoice Number: KAL-" + order.getId(),
                normalFont
        ));

        document.add(new Paragraph(
                "Order ID: #" + order.getId(),
                normalFont
        ));

        document.add(new Paragraph(
                "Order Date: " + orderDate,
                normalFont
        ));

        document.add(new Paragraph(
                "Order Status: " + order.getStatus(),
                normalFont
        ));

        document.add(new Paragraph(
                "Payment Status: " + order.getPaymentStatus(),
                normalFont
        ));

        /*
         * Cancellation date is not displayed because the current
         * Order entity does not contain a cancellation date field.
         * It can be added later using a cancelledAt field.
         */

        document.add(new Paragraph(" "));

        User customer = order.getUser();

        document.add(new Paragraph(
                "Customer Details",
                headingFont
        ));

        document.add(new Paragraph(
                "Name: " + customer.getName(),
                normalFont
        ));

        document.add(new Paragraph(
                "Email: " + customer.getEmail(),
                normalFont
        ));

        document.add(new Paragraph(" "));

        PdfPTable itemsTable = new PdfPTable(4);
        itemsTable.setWidthPercentage(100);
        itemsTable.setWidths(new float[]{4, 1, 2, 2});

        addHeaderCell(itemsTable, "Product");
        addHeaderCell(itemsTable, "Qty");
        addHeaderCell(itemsTable, "Price");
        addHeaderCell(itemsTable, "Amount");

        for (OrderItem item : order.getItems()) {

            String productName =
                    item.getProduct().getName();

            double price = item.getPrice();
            int quantity = item.getQuantity();
            double amount = price * quantity;

            itemsTable.addCell(
                    new PdfPCell(
                            new Phrase(productName, normalFont)
                    )
            );

            itemsTable.addCell(
                    new PdfPCell(
                            new Phrase(
                                    String.valueOf(quantity),
                                    normalFont
                            )
                    )
            );

            itemsTable.addCell(
                    new PdfPCell(
                            new Phrase(
                                    "Rs. " + String.format("%.2f", price),
                                    normalFont
                            )
                    )
            );

            itemsTable.addCell(
                    new PdfPCell(
                            new Phrase(
                                    "Rs. " + String.format(
                                            "%.2f",
                                            amount
                                    ),
                                    normalFont
                            )
                    )
            );
        }

        document.add(itemsTable);
        document.add(new Paragraph(" "));

        PdfPTable summaryTable = new PdfPTable(2);
        summaryTable.setWidthPercentage(100);
        summaryTable.setWidths(new float[]{4, 2});

        addSummaryRow(
                summaryTable,
                "Subtotal",
                order.getSubtotal(),
                normalFont
        );

        addSummaryRow(
                summaryTable,
                "GST",
                order.getGstAmount(),
                normalFont
        );

        addSummaryRow(
                summaryTable,
                "Shipping",
                order.getShippingCharge(),
                normalFont
        );

        addSummaryRow(
                summaryTable,
                "Discount",
                order.getDiscountAmount(),
                normalFont
        );

        addSummaryRow(
                summaryTable,
                "Total Paid",
                order.getTotalPrice(),
                headingFont
        );

        document.add(summaryTable);

        document.add(new Paragraph(" "));

        if (order.getStatus() == OrderStatus.CANCELLED) {

            Paragraph cancellationNote = new Paragraph(
                    "Note: The original paid amount is shown above. "
                            + "Order cancellation does not automatically "
                            + "mean that the refund has been completed.",
                    normalFont
            );

            document.add(cancellationNote);
            document.add(new Paragraph(" "));
        }

        Paragraph footer = new Paragraph(
                "Thank you for shopping with Kalakriti.",
                normalFont
        );

        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);

        document.close();

        return outputStream.toByteArray();
    }

    private void addHeaderCell(
            PdfPTable table,
            String text
    ) {
        Font font = FontFactory.getFont(
                FontFactory.HELVETICA_BOLD,
                10
        );

        PdfPCell cell = new PdfPCell(
                new Phrase(text, font)
        );

        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }

    private void addSummaryRow(
            PdfPTable table,
            String label,
            double value,
            Font font
    ) {
        table.addCell(
                new PdfPCell(
                        new Phrase(label, font)
                )
        );

        PdfPCell valueCell = new PdfPCell(
                new Phrase(
                        "Rs. " + String.format("%.2f", value),
                        font
                )
        );

        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(valueCell);
    }
}