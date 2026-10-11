package com.example.student_skill_exchange.controller;

import com.example.student_skill_exchange.dto.ErrorResponse;
import com.example.student_skill_exchange.dto.RatingResponse;
import com.example.student_skill_exchange.dto.RatingSummaryResponse;
import com.example.student_skill_exchange.dto.SubmitRatingRequest;
import com.example.student_skill_exchange.service.RatingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ratings")
public class RatingController {

    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    // POST http://localhost:8080/api/ratings
    @PostMapping
    public ResponseEntity<?> submitRating(@RequestBody SubmitRatingRequest dto) {
        try {
            RatingResponse created = ratingService.submitRating(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // GET http://localhost:8080/api/ratings/user/3
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getRatingsReceived(@PathVariable Long userId) {
        try {
            List<RatingResponse> ratings = ratingService.getRatingsReceived(userId);
            return ResponseEntity.ok(ratings);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    // GET http://localhost:8080/api/ratings/user/3/summary
    @GetMapping("/user/{userId}/summary")
    public ResponseEntity<?> getSummary(@PathVariable Long userId) {
        try {
            RatingSummaryResponse summary = ratingService.getSummary(userId);
            return ResponseEntity.ok(summary);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    // GET http://localhost:8080/api/ratings/given/2
    @GetMapping("/given/{userId}")
    public ResponseEntity<?> getRatingsGiven(@PathVariable Long userId) {
        try {
            List<RatingResponse> ratings = ratingService.getRatingsGiven(userId);
            return ResponseEntity.ok(ratings);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }
}