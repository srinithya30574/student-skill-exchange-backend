package com.example.student_skill_exchange.repository;

import com.example.student_skill_exchange.entity.ExchangeRequest;
import com.example.student_skill_exchange.entity.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExchangeRequestRepository extends JpaRepository<ExchangeRequest, Long> {

    List<ExchangeRequest> findByReceiverIdOrderByCreatedAtDesc(Long receiverId);

    List<ExchangeRequest> findByReceiverIdAndStatusOrderByCreatedAtDesc(Long receiverId, RequestStatus status);

    List<ExchangeRequest> findBySenderIdOrderByCreatedAtDesc(Long senderId);

    boolean existsBySenderIdAndReceiverIdAndStatus(Long senderId, Long receiverId, RequestStatus status);

    // NEW: requests I sent that have this status
    List<ExchangeRequest> findBySenderIdAndStatus(Long senderId, RequestStatus status);

    // NEW: requests I received that have this status
    List<ExchangeRequest> findByReceiverIdAndStatus(Long receiverId, RequestStatus status);
}