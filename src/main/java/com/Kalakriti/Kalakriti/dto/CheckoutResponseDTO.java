package com.Kalakriti.Kalakriti.dto;

import java.util.List;

public class CheckoutResponseDTO {

    private String message;
    private int ordersCreated;
    private double totalAmount;
    private List<Long> orderIds;

    public CheckoutResponseDTO(String message,
                               int ordersCreated,
                               double totalAmount,
                               List<Long> orderIds) {
        this.message = message;
        this.ordersCreated = ordersCreated;
        this.totalAmount = totalAmount;
        this.orderIds = orderIds;
    }

    public String getMessage() { return message; }
    public int getOrdersCreated() { return ordersCreated; }
    public double getTotalAmount() { return totalAmount; }
    public List<Long> getOrderIds() { return orderIds; }
}