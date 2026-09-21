package com.Kalakriti.Kalakriti.dto;

import java.time.LocalDateTime;

public class CheckoutOrderResponseDTO {

    private Long orderId;
    private String message;
    private double totalPrice;
    private String status;
    private String paymentStatus;
    private LocalDateTime createdAt;

    public CheckoutOrderResponseDTO(
            Long orderId,
            String message,
            double totalPrice,
            String paymentStatus
    ) {
        this.orderId = orderId;
        this.message = message;
        this.totalPrice = totalPrice;
        this.paymentStatus = paymentStatus;
    }

    public CheckoutOrderResponseDTO(
            Long orderId,
            double totalPrice,
            String status,
            String paymentStatus,
            LocalDateTime createdAt
    ) {
        this.orderId = orderId;
        this.totalPrice = totalPrice;
        this.status = status;
        this.paymentStatus = paymentStatus;
        this.createdAt = createdAt;
    }

    public Long getOrderId() {
        return orderId;
    }

    public String getMessage() {
        return message;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}