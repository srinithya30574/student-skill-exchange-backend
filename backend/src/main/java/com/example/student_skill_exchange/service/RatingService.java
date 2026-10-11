package com.example.student_skill_exchange.service;

import com.example.student_skill_exchange.dto.RatingResponse;
import com.example.student_skill_exchange.dto.RatingSummaryResponse;
import com.example.student_skill_exchange.dto.SubmitRatingRequest;
import com.example.student_skill_exchange.entity.Rating;
import com.example.student_skill_exchange.entity.RequestStatus;
import com.example.student_skill_exchange.entity.User;
import com.example.student_skill_exchange.repository.ExchangeRequestRepository;
import com.example.student_skill_exchange.repository.RatingRepository;
import com.example.student_skill_exchange.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class RatingService {

    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;
    private final ExchangeRequestRepository requestRepository;

    public RatingService(RatingRepository ratingRepository,
                         UserRepository userRepository,
                         ExchangeRequestRepository requestRepository) {
        this.ratingRepository = ratingRepository;
        this.userRepository = userRepository;
        this.requestRepository = requestRepository;
    }

    // ---------- GIVE A RATING ----------
    public RatingResponse submitRating(SubmitRatingRequest dto) {
        if (dto.getReviewerId() == null || dto.getReviewedUserId() == null) {
            throw new IllegalArgumentException("Reviewer and reviewed student are required");
        }
        if (dto.getReviewerId().equals(dto.getReviewedUserId())) {
            throw new IllegalArgumentException("You cannot rate yourself");
        }
        if (dto.getRating() == null || dto.getRating() < 1 || dto.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        if (dto.getFeedback() != null && dto.getFeedback().length() > 500) {
            throw new IllegalArgumentException("Feedback must be 500 characters or less");
        }

        User reviewer = userRepository.findById(dto.getReviewerId())
                .orElseThrow(() -> new IllegalArgumentException("Reviewer not found"));
        User reviewedUser = userRepository.findById(dto.getReviewedUserId())
                .orElseThrow(() -> new IllegalArgumentException("Student to rate not found"));

        if (!areConnected(reviewer.getId(), reviewedUser.getId())) {
            throw new IllegalArgumentException("You can only rate students you are connected with");
        }

        if (ratingRepository.existsByReviewerIdAndReviewedUserId(reviewer.getId(), reviewedUser.getId())) {
            throw new IllegalArgumentException("You have already rated this student");
        }

        String feedback = null;
        if (dto.getFeedback() != null && !dto.getFeedback().isBlank()) {
            feedback = dto.getFeedback().trim();
        }

        Rating rating = new Rating();
        rating.setReviewer(reviewer);
        rating.setReviewedUser(reviewedUser);
        rating.setRating(dto.getRating());
        rating.setFeedback(feedback);
        rating.setCreatedAt(LocalDateTime.now());

        return convertToResponse(ratingRepository.save(rating));
    }

    // ---------- RATINGS RECEIVED ----------
    public List<RatingResponse> getRatingsReceived(Long userId) {
        checkUserExists(userId);
        return convertList(ratingRepository.findByReviewedUserIdOrderByCreatedAtDescIdDesc(userId));
    }

    // ---------- RATINGS GIVEN ----------
    public List<RatingResponse> getRatingsGiven(Long userId) {
        checkUserExists(userId);
        return convertList(ratingRepository.findByReviewerIdOrderByCreatedAtDescIdDesc(userId));
    }

    // ---------- AVERAGE RATING ----------
    public RatingSummaryResponse getSummary(Long userId) {
        checkUserExists(userId);

        List<Rating> ratings = ratingRepository.findByReviewedUserIdOrderByCreatedAtDescIdDesc(userId);

        if (ratings.isEmpty()) {
            return new RatingSummaryResponse(userId, 0.0, 0);
        }

        int sum = 0;
        for (Rating rating : ratings) {
            sum += rating.getRating();
        }

        double average = (double) sum / ratings.size();
        average = Math.round(average * 10.0) / 10.0; // one decimal place

        return new RatingSummaryResponse(userId, average, ratings.size());
    }

    // ---------- HELPERS ----------
    // Connected = an ACCEPTED request exists in either direction
    private boolean areConnected(Long firstId, Long secondId) {
        return requestRepository.existsBySenderIdAndReceiverIdAndStatus(firstId, secondId, RequestStatus.ACCEPTED)
                || requestRepository.existsBySenderIdAndReceiverIdAndStatus(secondId, firstId, RequestStatus.ACCEPTED);
    }

    private void checkUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User not found");
        }
    }

    private List<RatingResponse> convertList(List<Rating> ratings) {
        List<RatingResponse> responses = new ArrayList<>();
        for (Rating rating : ratings) {
            responses.add(convertToResponse(rating));
        }
        return responses;
    }

    private RatingResponse convertToResponse(Rating rating) {
        return new RatingResponse(
                rating.getId(),
                rating.getReviewer().getId(),
                rating.getReviewer().getName(),
                rating.getReviewedUser().getId(),
                rating.getReviewedUser().getName(),
                rating.getRating(),
                rating.getFeedback(),
                rating.getCreatedAt()
        );
    }
}