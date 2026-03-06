package com.Kalakriti.Kalakriti.dto;

import java.time.LocalDateTime;

public class ReviewResponseDTO {

    private Long reviewId;
    private String userName;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;

    public ReviewResponseDTO(Long reviewId,
                             String userName,
                             int rating,
                             String comment,
                             LocalDateTime createdAt) {
        this.reviewId = reviewId;
        this.userName = userName;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    public Long getReviewId() { return reviewId; }
    public String getUserName() { return userName; }
    public int getRating() { return rating; }
    public String getComment() { return comment; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}