package com.Kalakriti.Kalakriti.dto;

import java.util.List;

public class CartResponseDTO {

    private Long cartId;
    private List<CartItemResponseDTO> items;
    private double grandTotal;

    public CartResponseDTO(Long cartId,
                           List<CartItemResponseDTO> items,
                           double grandTotal) {
        this.cartId = cartId;
        this.items = items;
        this.grandTotal = grandTotal;
    }

    public Long getCartId() { return cartId; }
    public List<CartItemResponseDTO> getItems() { return items; }
    public double getGrandTotal() { return grandTotal; }
}