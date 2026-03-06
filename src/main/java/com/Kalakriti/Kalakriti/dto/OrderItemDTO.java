package com.Kalakriti.Kalakriti.dto;

public class OrderItemDTO {

    private Long productId;
    private String productName;
    private int quantity;
    private double price;

    public OrderItemDTO(Long productId,
                        String productName,
                        int quantity,
                        double price) {

        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
    }

    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }
}