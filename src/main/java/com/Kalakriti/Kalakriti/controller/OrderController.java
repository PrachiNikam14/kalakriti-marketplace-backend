package com.Kalakriti.Kalakriti.controller;

import com.Kalakriti.Kalakriti.dto.CheckoutOrderResponseDTO;
import com.Kalakriti.Kalakriti.dto.OrderResponseDTO;
import com.Kalakriti.Kalakriti.dto.OrderStatusUpdateResponseDTO;
import com.Kalakriti.Kalakriti.entity.Order;
import com.Kalakriti.Kalakriti.entity.User;
import com.Kalakriti.Kalakriti.service.InvoiceService;
import com.Kalakriti.Kalakriti.service.OrderService;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user/orders")
public class OrderController {

    private final OrderService orderService;
    private final InvoiceService invoiceService;

    public OrderController(OrderService orderService, InvoiceService invoiceService) {
        this.orderService = orderService;
        this.invoiceService = invoiceService;
    }

    @GetMapping
    public List<OrderResponseDTO> getUserOrders(
            @AuthenticationPrincipal User user
    ) {
        return orderService.getUserOrders(user);
    }

    @PostMapping("/checkout")
    public ResponseEntity<CheckoutOrderResponseDTO> checkout(
            @AuthenticationPrincipal User user,
            @RequestParam Long addressId,
            @RequestParam(required = false) String couponCode
    ) {

        CheckoutOrderResponseDTO response =
                orderService.checkout(
                        user,
                        addressId,
                        couponCode
                );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/test")
    public String test() {
        return "OrderController working";
    }

    @PostMapping("/{orderId}/pay")
    public ResponseEntity<?> initiatePayment(
            @PathVariable Long orderId,  @AuthenticationPrincipal User user
    ) {
        try {
            return ResponseEntity.ok(
                    orderService.initiatePayment(orderId,user )
            );
        } catch (Exception e) {
            e.printStackTrace();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }


    @PostMapping("/verify-payment")
    public String verifyPayment(
            @RequestBody Map<String, String> request
    ) throws Exception {

        String razorpayOrderId =
                request.get("razorpay_order_id");

        String razorpayPaymentId =
                request.get("razorpay_payment_id");

        String razorpaySignature =
                request.get("razorpay_signature");

        return orderService.verifyPayment(
                razorpayOrderId,
                razorpayPaymentId,
                razorpaySignature
        );
    }
    @GetMapping("/pending")
    public ResponseEntity<?> getPendingOrder(
            @AuthenticationPrincipal User user
    ) {
        return orderService.getLatestPendingOrder(user)
                .map(order -> ResponseEntity.ok(
                        Map.of(
                                "orderId", order.getId(),
                                "totalPrice", order.getTotalPrice(),
                                "razorpayOrderId",
                                order.getRazorpayOrderId() == null
                                        ? ""
                                        : order.getRazorpayOrderId()
                        )
                ))
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @GetMapping("/{orderId}/invoice")
    public ResponseEntity<byte[]> downloadInvoice(
            @PathVariable Long orderId,
            @AuthenticationPrincipal User user
    ) throws Exception {

        byte[] invoice =
                invoiceService.generateInvoice(orderId, user);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=kalakriti-invoice-"
                                + orderId + ".pdf"
                )
                .contentType(MediaType.APPLICATION_PDF)
                .body(invoice);
    }

    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<?> cancelOrder(
            @PathVariable Long orderId,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(
                orderService.cancelOrder(orderId, user)
        );
    }
}