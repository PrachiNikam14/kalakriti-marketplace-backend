package com.Kalakriti.Kalakriti.service;

import com.Kalakriti.Kalakriti.dto.ArtisanOrderDTO;
import com.Kalakriti.Kalakriti.dto.OrderItemDTO;
import com.Kalakriti.Kalakriti.dto.OrderResponseDTO;
import com.Kalakriti.Kalakriti.dto.OrderStatusUpdateResponseDTO;
import com.Kalakriti.Kalakriti.entity.*;
import com.Kalakriti.Kalakriti.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final NotificationService notificationService;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final AddressRepository addressRepository;
    private final OrderItemRepository orderItemRepository;

    @Autowired
    private RazorpayService razorpayService;

    public OrderService(OrderRepository orderRepository,
                        ProductRepository productRepository,
                        NotificationService notificationService,
                        CartRepository cartRepository,
                        CartItemRepository cartItemRepository,
                        AddressRepository addressRepository,
                        OrderItemRepository orderItemRepository) {

        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.notificationService = notificationService;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.addressRepository = addressRepository;
        this.orderItemRepository = orderItemRepository;
    }

    // ================= USER ORDER HISTORY =================
    public List<OrderResponseDTO> getUserOrders(User user) {

        return orderRepository.findByUser(user)
                .stream()
                .map(order -> {

                    List<OrderItemDTO> items = order.getItems()
                            .stream()
                            .map(item -> new OrderItemDTO(
                                    item.getProduct().getId(),
                                    item.getProduct().getName(),
                                    item.getQuantity(),
                                    item.getPrice()
                            ))
                            .toList();

                    return new OrderResponseDTO(
                            order.getId(),
                            order.getTotalPrice(),
                            order.getStatus().name(),
                            order.getCreatedAt(),
                            items
                    );
                })
                .toList();
    }
    // ================= ADMIN UPDATE STATUS =================
    @Transactional
    public OrderStatusUpdateResponseDTO updateOrderStatus(Long orderId,
                                                          OrderStatus newStatus) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!isValidStatusTransition(order.getStatus(), newStatus)) {
            throw new RuntimeException(
                    "Invalid status transition from "
                            + order.getStatus() + " to " + newStatus
            );
        }

        order.setStatus(newStatus);
        orderRepository.save(order);

        if (newStatus == OrderStatus.SHIPPED) {
            notificationService.sendNotification(
                    order.getUser(),
                    NotificationType.ORDER_SHIPPED,
                    "Order Shipped",
                    "Your order #" + order.getId() + " has been shipped."
            );
        }

        if (newStatus == OrderStatus.DELIVERED) {
            notificationService.sendNotification(
                    order.getUser(),
                    NotificationType.ORDER_DELIVERED,
                    "Order Delivered",
                    "Your order #" + order.getId() + " has been delivered."
            );
        }

        return new OrderStatusUpdateResponseDTO(
                "Order status updated successfully",
                order.getId(),
                order.getStatus()
        );
    }

    // ================= USER CANCEL ORDER =================
    @Transactional
    public OrderStatusUpdateResponseDTO cancelOrder(User user, Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized action");
        }

        if (order.getStatus() != OrderStatus.PLACED) {
            throw new RuntimeException("Only PLACED orders can be cancelled");
        }

        // restore stock for each item
        for (OrderItem item : order.getItems()) {

            Product product = item.getProduct();

            product.setStockQuantity(
                    product.getStockQuantity() + item.getQuantity()
            );

            productRepository.save(product);
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        notificationService.sendNotification(
                user,
                NotificationType.ORDER_CANCELLED,
                "Order Cancelled",
                "Your order #" + order.getId() + " has been cancelled."
        );

        return new OrderStatusUpdateResponseDTO(
                "Order cancelled successfully",
                order.getId(),
                order.getStatus()
        );
    }

    // ================= STATUS TRANSITION LOGIC =================
    private boolean isValidStatusTransition(OrderStatus currentStatus,
                                            OrderStatus newStatus) {

        switch (currentStatus) {

            case PLACED:
                return newStatus == OrderStatus.SHIPPED
                        || newStatus == OrderStatus.CANCELLED;

            case SHIPPED:
                return newStatus == OrderStatus.DELIVERED;

            case DELIVERED:
            case CANCELLED:
                return false;

            default:
                return false;
        }
    }

    // ================= CHECKOUT =================
    @Transactional
    public String checkout(User user, Long addressId) {

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        if (!address.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized address");
        }

        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PLACED);

        order.setShippingFullName(address.getFullName());
        order.setShippingPhoneNumber(address.getPhoneNumber());
        order.setShippingLine1(address.getLine1());
        order.setShippingLine2(address.getLine2());
        order.setShippingCity(address.getCity());
        order.setShippingState(address.getState());
        order.setShippingPincode(address.getPincode());
        order.setShippingCountry(address.getCountry());

        orderRepository.save(order);

        double total = 0;

        for (CartItem item : cart.getItems()) {

            Product product = item.getProduct();

            if (!product.isActive()) {
                throw new RuntimeException("Product inactive");
            }

            if (product.getApprovalStatus() != ApprovalStatus.APPROVED) {
                throw new RuntimeException("Product not approved");
            }

            if (product.getStockQuantity() < item.getQuantity()) {
                throw new RuntimeException("Insufficient stock");
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(item.getQuantity());
            orderItem.setPrice(product.getPrice());

            orderItemRepository.save(orderItem);

            total += item.getQuantity() * product.getPrice();
        }

        order.setTotalPrice(total);
        orderRepository.save(order);

        cartItemRepository.deleteAll(cart.getItems());

        notificationService.sendNotification(
                user,
                NotificationType.ORDER_PLACED,
                "Order Confirmed",
                "Your order #" + order.getId() + " has been placed successfully."
        );

        return "Checkout successful. Order placed.";
    }

    // ================= PAYMENT INIT =================
    public String initiatePayment(Long orderId) throws Exception {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if(order.getRazorpayOrderId() != null){
            return "Payment already initiated";
        }

        if (PaymentStatus.PAID.equals(order.getPaymentStatus())) {
            throw new RuntimeException("Order already paid");
        }

        com.razorpay.Order razorpayOrder =
                razorpayService.createRazorpayOrder(order.getTotalPrice());

        order.setRazorpayOrderId(razorpayOrder.get("id"));
        orderRepository.save(order);

        return razorpayOrder.toString();
    }

    // ================= VERIFY PAYMENT =================
    @Transactional
    public String verifyPayment(String razorpayOrderId,
                                String razorpayPaymentId,
                                String razorpaySignature) throws Exception {

        Order order = orderRepository
                .findByRazorpayOrderId(razorpayOrderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (PaymentStatus.PAID.equals(order.getPaymentStatus())) {
            return "Payment already verified";
        }

        boolean isValid = razorpayService.verifySignature(
                razorpayOrderId,
                razorpayPaymentId,
                razorpaySignature
        );

        if (!isValid) {
            throw new RuntimeException("Invalid payment signature");
        }

        order.setPaymentStatus(PaymentStatus.PAID);
        order.setRazorpayPaymentId(razorpayPaymentId);

        for (OrderItem item : order.getItems()) {

            Product product = item.getProduct();

            product.setStockQuantity(
                    product.getStockQuantity() - item.getQuantity()
            );

            productRepository.save(product);
        }

        orderRepository.save(order);

        return "Payment verified successfully";
    }

    //Fetch Order for Artisans
    public Page<ArtisanOrderDTO> getOrdersForArtisan(Long artisanId, Pageable pageable) {
        return orderItemRepository.findOrdersForArtisan(artisanId, pageable);
    }
}