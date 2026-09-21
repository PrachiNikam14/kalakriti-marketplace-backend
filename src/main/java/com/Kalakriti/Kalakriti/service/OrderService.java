package com.Kalakriti.Kalakriti.service;

import com.Kalakriti.Kalakriti.dto.*;
import com.Kalakriti.Kalakriti.entity.*;
import com.Kalakriti.Kalakriti.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.*;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final NotificationService notificationService;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final AddressRepository addressRepository;
    private final OrderItemRepository orderItemRepository;
    private final RazorpayService razorpayService;

    @Autowired
    public OrderService(
            OrderRepository orderRepository,
            ProductRepository productRepository,
            NotificationService notificationService,
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            AddressRepository addressRepository,
            OrderItemRepository orderItemRepository,
            RazorpayService razorpayService
    ) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.notificationService = notificationService;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.addressRepository = addressRepository;
        this.orderItemRepository = orderItemRepository;
        this.razorpayService = razorpayService;
    }

    // ================= USER ORDER HISTORY =================

    public List<OrderResponseDTO> getUserOrders(User user) {
        return orderRepository
                .findByUserAndPaymentStatusOrderByCreatedAtDesc(
                        user,
                        PaymentStatus.PAID
                )
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
                            order.getSubtotal(),
                            order.getGstAmount(),
                            order.getShippingCharge(),
                            order.getDiscountAmount(),
                            order.getTotalPrice(),
                            order.getCouponCode(),
                            order.getStatus().name(),
                            order.getCreatedAt(),
                            items
                    );
                })
                .toList();
    }

    // ================= ADMIN UPDATE STATUS =================

    @Transactional
    public OrderStatusUpdateResponseDTO updateOrderStatus(
            Long orderId,
            OrderStatus newStatus
    ) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new RuntimeException("Order not found")
                );

        if (!isValidStatusTransition(order.getStatus(), newStatus)) {
            throw new RuntimeException(
                    "Invalid status transition from "
                            + order.getStatus()
                            + " to "
                            + newStatus
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
    public String cancelOrder(Long orderId, User user) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new RuntimeException("Order not found")
                );

        if (order.getUser() == null
                || !order.getUser().getId().equals(user.getId())) {

            throw new RuntimeException(
                    "You are not authorized to cancel this order"
            );
        }

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new RuntimeException("Order is already cancelled");
        }

        if (order.getStatus() == OrderStatus.SHIPPED
                || order.getStatus() == OrderStatus.DELIVERED) {

            throw new RuntimeException(
                    "This order cannot be cancelled now"
            );
        }

        for (OrderItem item : order.getItems()) {

            Product product = item.getProduct();

            product.setStockQuantity(
                    product.getStockQuantity() + item.getQuantity()
            );
        }

        order.setStatus(OrderStatus.CANCELLED);

        orderRepository.save(order);

        return "Order cancelled successfully";
    }

    // ================= STATUS TRANSITION LOGIC =================

    private boolean isValidStatusTransition(
            OrderStatus currentStatus,
            OrderStatus newStatus
    ) {

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
    public CheckoutOrderResponseDTO checkout(
            User user,
            Long addressId,
            String couponCode
    ) {

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() ->
                        new RuntimeException("Address not found")
                );

        if (!address.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized address");
        }

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() ->
                        new RuntimeException("Cart is empty")
                );

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        Order order = new Order();

        order.setUser(user);
        order.setStatus(OrderStatus.PLACED);
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setCouponCode(couponCode);

        order.setShippingFullName(address.getFullName());
        order.setShippingPhoneNumber(address.getPhoneNumber());
        order.setShippingLine1(address.getLine1());
        order.setShippingLine2(address.getLine2());
        order.setShippingCity(address.getCity());
        order.setShippingState(address.getState());
        order.setShippingPincode(address.getPincode());
        order.setShippingCountry(address.getCountry());

        double subtotal = 0.0;

        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cart.getItems()) {

            Product product = cartItem.getProduct();

            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new RuntimeException(
                        "Insufficient stock for " + product.getName()
                );
            }

            OrderItem orderItem = new OrderItem();

            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(product.getPrice());

            orderItems.add(orderItem);

            subtotal += product.getPrice() * cartItem.getQuantity();
        }

        double gstAmount = subtotal * 0.18;
        double shippingCharge = 0.0;
        double discountAmount = 0.0;

        double totalPrice =
                subtotal
                        + gstAmount
                        + shippingCharge
                        - discountAmount;

        order.setItems(orderItems);
        order.setSubtotal(subtotal);
        order.setGstAmount(gstAmount);
        order.setShippingCharge(shippingCharge);
        order.setDiscountAmount(discountAmount);
        order.setTotalPrice(totalPrice);

        Order savedOrder = orderRepository.save(order);

        return new CheckoutOrderResponseDTO(
                savedOrder.getId(),
                "Order created successfully",
                savedOrder.getTotalPrice(),
                savedOrder.getPaymentStatus().name()
        );
    }

    // ================= PAYMENT INIT =================

    // ================= PAYMENT INIT =================

    @Transactional
    public Map<String, Object> initiatePayment(Long orderId, User user) throws Exception {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new RuntimeException("Order not found")
                );

        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You are not authorized to pay for this order");
        }

        if (order.getPaymentStatus() == PaymentStatus.PAID) {
            throw new RuntimeException("Order already paid");
        }

        com.razorpay.Order razorpayOrder =
                razorpayService.createRazorpayOrder(order);

        if (razorpayOrder == null) {
            throw new RuntimeException(
                    "Razorpay order creation failed"
            );
        }

        Object razorpayIdObject = razorpayOrder.get("id");
        Object razorpayAmountObject = razorpayOrder.get("amount");
        Object razorpayCurrencyObject = razorpayOrder.get("currency");

        if (razorpayIdObject == null
                || razorpayAmountObject == null
                || razorpayCurrencyObject == null) {

            throw new RuntimeException(
                    "Invalid response received from Razorpay"
            );
        }

        String razorpayOrderId =
                String.valueOf(razorpayIdObject);

        long amount =
                Long.parseLong(
                        String.valueOf(razorpayAmountObject)
                );

        String currency =
                String.valueOf(razorpayCurrencyObject);

        order.setRazorpayOrderId(razorpayOrderId);
        orderRepository.save(order);

        Map<String, Object> response = new HashMap<>();

        response.put("id", razorpayOrderId);
        response.put("amount", amount);
        response.put("currency", currency);
        response.put("orderId", order.getId());

        return response;
    }

    // ================= VERIFY PAYMENT =================

    @Transactional
    public String verifyPayment(
            String razorpayOrderId,
            String razorpayPaymentId,
            String razorpaySignature
    ) throws Exception {

        Order order = orderRepository
                .findByRazorpayOrderId(razorpayOrderId)
                .orElseThrow(() ->
                        new RuntimeException("Order not found")
                );

        if (PaymentStatus.PAID.equals(order.getPaymentStatus())) {
            return "Payment already verified";
        }

        boolean isValid = razorpayService.verifySignature(
                razorpayOrderId,
                razorpayPaymentId,
                razorpaySignature
        );

        if (!isValid) {
            throw new RuntimeException(
                    "Invalid payment signature"
            );
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
        clearCart(order.getUser());

        return "Payment verified successfully";
    }

    // ================= FETCH ORDERS FOR ARTISAN =================

    public Page<ArtisanOrderDTO> getOrdersForArtisan(
            Long artisanId,
            Pageable pageable
    ) {
        return orderItemRepository.findOrdersForArtisan(
                artisanId,
                pageable
        );
    }

    // ================= FETCH ORDERS FOR USER =================

    public Page<UserOrderDTO> getOrdersForUser(
            Long userId,
            Pageable pageable
    ) {
        return orderItemRepository.findOrdersForUser(
                userId,
                pageable
        );
    }

    // ================= ARTISAN UPDATE ORDER STATUS =================

    public void updateOrderStatus(
            Long orderId,
            Long artisanId,
            OrderStatus status
    ) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new RuntimeException("Order not found")
                );

        boolean artisanOwnsProduct = order.getItems()
                .stream()
                .anyMatch(item ->
                        item.getProduct()
                                .getArtisan()
                                .getId()
                                .equals(artisanId)
                );

        if (!artisanOwnsProduct) {
            throw new RuntimeException(
                    "You cannot update this order"
            );
        }

        order.setStatus(status);
        orderRepository.save(order);
    }

    // ================= CLEAR CART AFTER PAYMENT =================

    @Transactional
    public void clearCart(User user) {

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() ->
                        new RuntimeException("Cart not found")
                );

        cartItemRepository.deleteAll(cart.getItems());

        cart.getItems().clear();

        cartRepository.save(cart);
    }

    @Transactional(readOnly = true)
    public Optional<Order> getLatestPendingOrder(User user) {
        return orderRepository
                .findFirstByUserAndPaymentStatusOrderByCreatedAtDesc(
                        user,
                        PaymentStatus.PENDING
                );
    }
}