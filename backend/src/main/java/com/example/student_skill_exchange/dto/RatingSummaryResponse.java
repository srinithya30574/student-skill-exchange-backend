package com.example.student_skill_exchange.dto;

public class RatingSummaryResponse {

    private Long userId;
    private double averageRating;
    private int totalRatings;

    public RatingSummaryResponse() {
    }

    public RatingSummaryResponse(Long userId, double averageRating, int totalRatings) {
        this.userId = userId;
        this.averageRating = averageRating;
        this.totalRatings = totalRatings;
    }

    public Long getUserId() {
        return userId;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public int getTotalRatings() {
        return totalRatings;
    }
}