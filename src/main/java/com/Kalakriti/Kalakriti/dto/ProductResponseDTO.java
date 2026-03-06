package com.Kalakriti.Kalakriti.dto;

import java.time.LocalDateTime;

public class ProductResponseDTO {

    private Long id;
    private String name;
    private String description;
    private double price;
    private String category;
    private String imageUrl;
    private LocalDateTime createdAt;

    private Long artisanId;
    private String artisanName;
    private String artisanEmail;
    private double averageRating;
    private long reviewCount;

    public ProductResponseDTO(
            Long id,
            String name,
            String description,
            double price,
            String category,
            String imageUrl,
            LocalDateTime createdAt,
            Long artisanId,
            String artisanName,
            String artisanEmail,
            double averageRating,
            long reviewCount
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.imageUrl = imageUrl;
        this.createdAt = createdAt;
        this.artisanId = artisanId;
        this.artisanName = artisanName;
        this.artisanEmail = artisanEmail;
        this.averageRating = averageRating;
        this.reviewCount = reviewCount;
    }

    // getters only
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public String getCategory() { return category; }
    public String getImageUrl() { return imageUrl; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Long getArtisanId() { return artisanId; }
    public String getArtisanName() { return artisanName; }
    public String getArtisanEmail() { return artisanEmail; }
    public double getAverageRating() { return averageRating; }
    public long getReviewCount() { return reviewCount; }
}