package com.Kalakriti.Kalakriti.dto;

import java.time.LocalDateTime;
import java.util.List;

public class OrderResponseDTO {

    private Long orderId;
    private double subtotal;
    private double gstAmount;
    private double shippingCharge;
    private double discountAmount;
    private Double totalPrice;
    private String couponCode;
    private String status;
    private LocalDateTime createdAt;
    private List<OrderItemDTO> items;

    // ✅ Full constructor with all fields
    public OrderResponseDTO(Long orderId,
                            double subtotal,
                            double gstAmount,
                            double shippingCharge,
                            double discountAmount,
                            Double totalPrice,
                            String couponCode,
                            String status,
                            LocalDateTime createdAt,
                            List<OrderItemDTO> items) {
        this.orderId = orderId;
        this.subtotal = subtotal;
        this.gstAmount = gstAmount;
        this.shippingCharge = shippingCharge;
        this.discountAmount = discountAmount;
        this.totalPrice = totalPrice;
        this.couponCode = couponCode;
        this.status = status;
        this.createdAt = createdAt;
        this.items = items;
    }

    // ✅ Getters
    public Long getOrderId() { return orderId; }
    public double getSubtotal() { return subtotal; }
    public double getGstAmount() { return gstAmount; }
    public double getShippingCharge() { return shippingCharge; }
    public double getDiscountAmount() { return discountAmount; }
    public double getTotalPrice() { return totalPrice; }
    public String getCouponCode() { return couponCode; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public List<OrderItemDTO> getItems() { return items; }
}
