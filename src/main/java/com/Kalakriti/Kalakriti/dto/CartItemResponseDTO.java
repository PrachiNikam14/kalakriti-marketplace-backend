package com.Kalakriti.Kalakriti.dto;

public class CartItemResponseDTO {

    private Long cartItemId;
    private Long productId;
    private String productName;
    private double price;
    private int quantity;
    private double totalPrice;

    public CartItemResponseDTO(Long cartItemId,
                               Long productId,
                               String productName,
                               double price,
                               int quantity,
                               double totalPrice) {
        this.cartItemId = cartItemId;
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }

    public Long getCartItemId() { return cartItemId; }
    public Long getProductId() { return productId; }
    public String getProductName() { return productName; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public double getTotalPrice() { return totalPrice; }
}