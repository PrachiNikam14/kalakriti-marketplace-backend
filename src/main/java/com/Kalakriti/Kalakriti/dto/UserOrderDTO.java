package com.Kalakriti.Kalakriti.dto;

import com.Kalakriti.Kalakriti.entity.OrderStatus;
import java.time.LocalDateTime;

public class UserOrderDTO {

    private Long orderId;
    private String productName;
    private int quantity;
    private double price;
    private OrderStatus orderStatus;
    private LocalDateTime orderedAt;

    public UserOrderDTO(Long orderId, String productName, int quantity,
                        double price, OrderStatus orderStatus,
                        LocalDateTime orderedAt) {
        this.orderId = orderId;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.orderStatus = orderStatus;
        this.orderedAt = orderedAt;
    }

    public Long getOrderId() { return orderId; }
    public String getProductName() { return productName; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public OrderStatus getOrderStatus() { return orderStatus; }
    public LocalDateTime getOrderedAt() { return orderedAt; }
}