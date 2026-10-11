package com.example.student_skill_exchange.repository;

import com.example.student_skill_exchange.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    List<Rating> findByReviewedUserIdOrderByCreatedAtDescIdDesc(Long reviewedUserId);

    List<Rating> findByReviewerIdOrderByCreatedAtDescIdDesc(Long reviewerId);

    boolean existsByReviewerIdAndReviewedUserId(Long reviewerId, Long reviewedUserId);
}