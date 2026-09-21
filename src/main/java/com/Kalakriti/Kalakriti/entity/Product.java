package com.Kalakriti.Kalakriti.entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import jakarta.persistence.*;
import org.apache.logging.log4j.spi.ThreadContextMap;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private double price;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductImageUrl> imageUrls;


    @Enumerated(EnumType.STRING)
    private CategoryType category;

    @ManyToOne
    @JoinColumn(name = "artisan_id", nullable = false)
    private User artisan;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @Enumerated(EnumType.STRING)
    private ApprovalStatus approvalStatus;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private int stockQuantity;

    private Double rating;
    private Integer reviewCount;

    // ===== Getters & Setters =====

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public List<ProductImageUrl> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<ProductImageUrl> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public CategoryType getCategory() {
        return category;
    }

    public void setCategory(CategoryType category) {
        this.category = category;
    }

    public User getArtisan() {
        return artisan;
    }

    public void setArtisan(User artisan) {
        this.artisan = artisan;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public ApprovalStatus getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(ApprovalStatus approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public long getReviewCount() {
        return reviewCount;
    }

    public double getAverageRating() {
        return rating;
    }


    public ThreadContextMap getImageUrl() {
        return (ThreadContextMap) imageUrls;
    }
}