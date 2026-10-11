package com.example.student_skill_exchange.dto;

import java.time.LocalDateTime;

public class RatingResponse {

    private Long id;
    private Long reviewerId;
    private String reviewerName;
    private Long reviewedUserId;
    private String reviewedUserName;
    private int rating;
    private String feedback;
    private LocalDateTime createdAt;

    public RatingResponse() {
    }

    public RatingResponse(Long id, Long reviewerId, String reviewerName,
                          Long reviewedUserId, String reviewedUserName,
                          int rating, String feedback, LocalDateTime createdAt) {
        this.id = id;
        this.reviewerId = reviewerId;
        this.reviewerName = reviewerName;
        this.reviewedUserId = reviewedUserId;
        this.reviewedUserName = reviewedUserName;
        this.rating = rating;
        this.feedback = feedback;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Long getReviewerId() {
        return reviewerId;
    }

    public String getReviewerName() {
        return reviewerName;
    }

    public Long getReviewedUserId() {
        return reviewedUserId;
    }

    public String getReviewedUserName() {
        return reviewedUserName;
    }

    public int getRating() {
        return rating;
    }

    public String getFeedback() {
        return feedback;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}