package com.example.student_skill_exchange.dto;

public class SubmitRatingRequest {

    private Long reviewerId;
    private Long reviewedUserId;
    private Integer rating;
    private String feedback;

    public SubmitRatingRequest() {
    }

    public Long getReviewerId() {
        return reviewerId;
    }

    public void setReviewerId(Long reviewerId) {
        this.reviewerId = reviewerId;
    }

    public Long getReviewedUserId() {
        return reviewedUserId;
    }

    public void setReviewedUserId(Long reviewedUserId) {
        this.reviewedUserId = reviewedUserId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}