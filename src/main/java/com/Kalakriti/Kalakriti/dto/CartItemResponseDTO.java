package com.Kalakriti.Kalakriti.dto;

import java.util.List;

public class CartItemResponseDTO {

    private Long cartItemId;
    private Long productId;
    private String productName;
    private double price;
    private int quantity;
    private double totalPrice;
    private String imageUrl;

    public CartItemResponseDTO(Long cartItemId,
                               Long productId,
                               String productName,
                               double price,
                               int quantity,
                               double totalPrice,
                               String imageUrl) {
        this.cartItemId = cartItemId;
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.imageUrl = imageUrl;
    }

    public Long getCartItemId() { return cartItemId; }
    public Long getProductId() { return productId; }
    public String getProductName() { return productName; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public double getTotalPrice() { return totalPrice; }
    public String getImageUrl() { return imageUrl; }
}