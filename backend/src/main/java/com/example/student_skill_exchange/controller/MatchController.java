package com.example.student_skill_exchange.controller;

import com.example.student_skill_exchange.dto.ErrorResponse;
import com.example.student_skill_exchange.dto.MatchResponse;
import com.example.student_skill_exchange.service.MatchService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/matches")
public class MatchController {

    private final MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    // GET http://localhost:8080/api/matches/2
    @GetMapping("/{userId}")
    public ResponseEntity<?> getMatches(@PathVariable Long userId) {
        try {
            List<MatchResponse> matches = matchService.getMatches(userId);
            return ResponseEntity.ok(matches);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    // GET http://localhost:8080/api/matches/2/with/3
    @GetMapping("/{userId}/with/{otherUserId}")
    public ResponseEntity<?> getMatchWith(@PathVariable Long userId,
                                          @PathVariable Long otherUserId) {
        try {
            MatchResponse match = matchService.getMatchWith(userId, otherUserId);
            return ResponseEntity.ok(match);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }
}