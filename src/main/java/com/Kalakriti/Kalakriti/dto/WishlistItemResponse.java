package com.Kalakriti.Kalakriti.dto;

public class WishlistItemResponse {

    private Long id;
    private ProductWishlistResponse product;

    public WishlistItemResponse() {
    }

    public WishlistItemResponse(Long id, ProductWishlistResponse product) {
        this.id = id;
        this.product = product;
    }

    public Long getId() {
        return id;
    }

    public ProductWishlistResponse getProduct() {
        return product;
    }
}