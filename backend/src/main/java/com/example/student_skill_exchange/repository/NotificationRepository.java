package com.example.student_skill_exchange.repository;

import com.example.student_skill_exchange.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserIdOrderByCreatedAtDescIdDesc(Long userId);

    List<Notification> findByUserIdAndReadFalse(Long userId);

    long countByUserIdAndReadFalse(Long userId);
}