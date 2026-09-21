package com.Kalakriti.Kalakriti.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ProductResponseDTO {
    private Long id;
    private String name;
    private String description;
    private double price;
    private String category;
    private String imageUrl;        // first image
    private List<String> imageUrls; // full list
    private LocalDateTime createdAt;
    private Long artisanId;
    private String artisanName;
    private String artisanEmail;
    private double averageRating;
    private long reviewCount;
    private int stockQuantity;

    // ✅ Constructor with explicit fields
    public ProductResponseDTO(
            Long id,
            String name,
            String description,
            double price,
            String category,
            String imageUrl,
            List<String> imageUrls,
            LocalDateTime createdAt,
            Long artisanId,
            String artisanName,
            String artisanEmail,
            double averageRating,
            long reviewCount, int stockQuantity

    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.imageUrl = imageUrl;
        this.imageUrls = imageUrls;
        this.createdAt = createdAt;
        this.artisanId = artisanId;
        this.artisanName = artisanName;
        this.artisanEmail = artisanEmail;
        this.averageRating = averageRating;
        this.reviewCount = reviewCount;
        this.stockQuantity = stockQuantity;
    }

    // ✅ Getters and setters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public String getCategory() { return category; }
    public String getImageUrl() { return imageUrl; }
    public List<String> getImageUrls() { return imageUrls; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Long getArtisanId() { return artisanId; }
    public String getArtisanName() { return artisanName; }
    public String getArtisanEmail() { return artisanEmail; }
    public double getAverageRating() { return averageRating; }
    public long getReviewCount() { return reviewCount; }
    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }



    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(double price) { this.price = price; }
    public void setCategory(String category) { this.category = category; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setArtisanId(Long artisanId) { this.artisanId = artisanId; }
    public void setArtisanName(String artisanName) { this.artisanName = artisanName; }
    public void setArtisanEmail(String artisanEmail) { this.artisanEmail = artisanEmail; }
    public void setAverageRating(double averageRating) { this.averageRating = averageRating; }
    public void setReviewCount(long reviewCount) { this.reviewCount = reviewCount; }
}
