package com.example.student_skill_exchange.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "ratings",
        uniqueConstraints = @UniqueConstraint(columnNames = {"reviewer_id", "reviewed_user_id"})
)
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "reviewer_id")
    private User reviewer;

    @ManyToOne(optional = false)
    @JoinColumn(name = "reviewed_user_id")
    private User reviewedUser;

    @Column(nullable = false)
    private int rating;

    @Column(length = 500)
    private String feedback;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public Rating() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getReviewer() {
        return reviewer;
    }

    public void setReviewer(User reviewer) {
        this.reviewer = reviewer;
    }

    public User getReviewedUser() {
        return reviewedUser;
    }

    public void setReviewedUser(User reviewedUser) {
        this.reviewedUser = reviewedUser;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}