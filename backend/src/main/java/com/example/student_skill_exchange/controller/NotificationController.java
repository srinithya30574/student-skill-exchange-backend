package com.example.student_skill_exchange.controller;

import com.example.student_skill_exchange.dto.ErrorResponse;
import com.example.student_skill_exchange.dto.NotificationResponse;
import com.example.student_skill_exchange.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // GET http://localhost:8080/api/notifications/user/2
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getNotifications(@PathVariable Long userId) {
        try {
            List<NotificationResponse> notifications = notificationService.getNotifications(userId);
            return ResponseEntity.ok(notifications);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    // GET http://localhost:8080/api/notifications/user/2/unread-count
    @GetMapping("/user/{userId}/unread-count")
    public ResponseEntity<?> getUnreadCount(@PathVariable Long userId) {
        try {
            long count = notificationService.getUnreadCount(userId);
            return ResponseEntity.ok(Map.of("unreadCount", count));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    // PUT http://localhost:8080/api/notifications/5/read?userId=2
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long notificationId,
                                        @RequestParam Long userId) {
        try {
            return ResponseEntity.ok(notificationService.markAsRead(notificationId, userId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // PUT http://localhost:8080/api/notifications/user/2/read-all
    @PutMapping("/user/{userId}/read-all")
    public ResponseEntity<?> markAllAsRead(@PathVariable Long userId) {
        try {
            notificationService.markAllAsRead(userId);
            return ResponseEntity.ok(Map.of("message", "All notifications marked as read"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }
}