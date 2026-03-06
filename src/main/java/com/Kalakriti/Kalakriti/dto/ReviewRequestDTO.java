package com.Kalakriti.Kalakriti.dto;

public class ReviewRequestDTO {

    private int rating;
    private String comment;

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}