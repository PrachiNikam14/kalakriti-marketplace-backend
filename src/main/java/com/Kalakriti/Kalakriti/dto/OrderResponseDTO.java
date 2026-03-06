package com.Kalakriti.Kalakriti.dto;

import java.time.LocalDateTime;
import java.util.List;

public class OrderResponseDTO {

    private Long orderId;
    private double totalPrice;
    private String status;
    private LocalDateTime createdAt;
    private List<OrderItemDTO> items;

    public OrderResponseDTO(Long orderId,
                            double totalPrice,
                            String status,
                            LocalDateTime createdAt,
                            List<OrderItemDTO> items) {

        this.orderId = orderId;
        this.totalPrice = totalPrice;
        this.status = status;
        this.createdAt = createdAt;
        this.items = items;
    }

    public Long getOrderId() {
        return orderId;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<OrderItemDTO> getItems() {
        return items;
    }
}