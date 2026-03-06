package com.Kalakriti.Kalakriti.dto;

import java.time.LocalDateTime;
import com.Kalakriti.Kalakriti.entity.OrderStatus;

public class ArtisanOrderDTO {

    private Long orderId;
    private String productName;
    private int quantity;
    private double price;
    private String buyerName;
    private OrderStatus orderStatus;
    private LocalDateTime orderedAt;

    public ArtisanOrderDTO(
            Long orderId,
            String productName,
            int quantity,
            double price,
            String buyerName,
            OrderStatus orderStatus,
            LocalDateTime orderedAt) {

        this.orderId = orderId;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.buyerName = buyerName;
        this.orderStatus = orderStatus;
        this.orderedAt = orderedAt;
    }

    public Long getOrderId() { return orderId; }
    public String getProductName() { return productName; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public String getBuyerName() { return buyerName; }
    public OrderStatus getOrderStatus() { return orderStatus; }
    public LocalDateTime getOrderedAt() { return orderedAt; }
}