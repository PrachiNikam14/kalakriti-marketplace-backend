package com.Kalakriti.Kalakriti.dto;

public class ProductWishlistResponse {

    private Long id;
    private String name;
    private double price;
    private String imageUrl;
    private int stockQuantity;

    public ProductWishlistResponse() {
    }

    public ProductWishlistResponse(
            Long id,
            String name,
            double price,
            String imageUrl,
            int stockQuantity) {

        this.id = id;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.stockQuantity = stockQuantity;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }
}