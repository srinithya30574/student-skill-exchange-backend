package com.example.student_skill_exchange.controller;

import com.example.student_skill_exchange.dto.ErrorResponse;
import com.example.student_skill_exchange.dto.RequestResponse;
import com.example.student_skill_exchange.dto.SendExchangeRequest;
import com.example.student_skill_exchange.entity.RequestStatus;
import com.example.student_skill_exchange.service.RequestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/requests")
public class RequestController {

    private final RequestService requestService;

    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    // POST http://localhost:8080/api/requests
    @PostMapping
    public ResponseEntity<?> sendRequest(@RequestBody SendExchangeRequest dto) {
        try {
            RequestResponse created = requestService.sendRequest(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // GET http://localhost:8080/api/requests/received/3
    // GET http://localhost:8080/api/requests/received/3?status=PENDING
    @GetMapping("/received/{userId}")
    public ResponseEntity<?> getReceivedRequests(@PathVariable Long userId,
                                                 @RequestParam(required = false) RequestStatus status) {
        try {
            List<RequestResponse> requests = requestService.getReceivedRequests(userId, status);
            return ResponseEntity.ok(requests);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    // GET http://localhost:8080/api/requests/sent/2
    @GetMapping("/sent/{userId}")
    public ResponseEntity<?> getSentRequests(@PathVariable Long userId) {
        try {
            List<RequestResponse> requests = requestService.getSentRequests(userId);
            return ResponseEntity.ok(requests);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    // PUT http://localhost:8080/api/requests/1/accept?userId=3
    @PutMapping("/{requestId}/accept")
    public ResponseEntity<?> acceptRequest(@PathVariable Long requestId,
                                           @RequestParam Long userId) {
        try {
            return ResponseEntity.ok(requestService.acceptRequest(requestId, userId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // PUT http://localhost:8080/api/requests/1/reject?userId=3
    @PutMapping("/{requestId}/reject")
    public ResponseEntity<?> rejectRequest(@PathVariable Long requestId,
                                           @RequestParam Long userId) {
        try {
            return ResponseEntity.ok(requestService.rejectRequest(requestId, userId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }
}