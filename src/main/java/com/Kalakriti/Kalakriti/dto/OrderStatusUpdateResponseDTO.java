package com.Kalakriti.Kalakriti.dto;

import com.Kalakriti.Kalakriti.entity.OrderStatus;

public class OrderStatusUpdateResponseDTO {

    private String message;
    private Long orderId;
    private String newStatus;

    public OrderStatusUpdateResponseDTO(String message,
                                        Long orderId,
                                        OrderStatus newStatus) {
        this.message = message;
        this.orderId = orderId;
        this.newStatus = newStatus.name();
    }

    public String getMessage() {
        return message;
    }

    public Long getOrderId() {
        return orderId;
    }

    public String getNewStatus() {
        return newStatus;
    }
}