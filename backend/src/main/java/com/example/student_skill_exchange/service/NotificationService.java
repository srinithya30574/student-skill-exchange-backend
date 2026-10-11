package com.example.student_skill_exchange.service;

import com.example.student_skill_exchange.dto.NotificationResponse;
import com.example.student_skill_exchange.entity.Notification;
import com.example.student_skill_exchange.entity.NotificationType;
import com.example.student_skill_exchange.entity.User;
import com.example.student_skill_exchange.repository.NotificationRepository;
import com.example.student_skill_exchange.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository,
                               UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    // ---------- CREATE (called by RequestService, not by the frontend) ----------
    public void createNotification(User user, String message, NotificationType type) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);
        notification.setType(type);
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }

    // ---------- LIST ----------
    public List<NotificationResponse> getNotifications(Long userId) {
        checkUserExists(userId);

        List<NotificationResponse> responses = new ArrayList<>();
        for (Notification notification
                : notificationRepository.findByUserIdOrderByCreatedAtDescIdDesc(userId)) {
            responses.add(convertToResponse(notification));
        }
        return responses;
    }

    // ---------- UNREAD COUNT ----------
    public long getUnreadCount(Long userId) {
        checkUserExists(userId);
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }

    // ---------- MARK ONE AS READ ----------
    public NotificationResponse markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));

        if (!notification.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("This notification does not belong to this user");
        }

        notification.setRead(true);
        return convertToResponse(notificationRepository.save(notification));
    }

    // ---------- MARK ALL AS READ ----------
    public void markAllAsRead(Long userId) {
        checkUserExists(userId);

        List<Notification> unread = notificationRepository.findByUserIdAndReadFalse(userId);
        for (Notification notification : unread) {
            notification.setRead(true);
        }
        notificationRepository.saveAll(unread);
    }

    // ---------- HELPERS ----------
    private void checkUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User not found");
        }
    }

    private NotificationResponse convertToResponse(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getMessage(),
                notification.getType(),
                notification.isRead(),
                notification.getCreatedAt()
        );
    }
}