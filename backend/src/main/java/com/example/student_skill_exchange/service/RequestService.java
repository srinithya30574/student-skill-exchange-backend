package com.example.student_skill_exchange.service;

import com.example.student_skill_exchange.dto.RequestResponse;
import com.example.student_skill_exchange.dto.SendExchangeRequest;
import com.example.student_skill_exchange.entity.ExchangeRequest;
import com.example.student_skill_exchange.entity.NotificationType;
import com.example.student_skill_exchange.entity.RequestStatus;
import com.example.student_skill_exchange.entity.User;
import com.example.student_skill_exchange.repository.ExchangeRequestRepository;
import com.example.student_skill_exchange.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class RequestService {

    private final ExchangeRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService; // NEW

    public RequestService(ExchangeRequestRepository requestRepository,
                          UserRepository userRepository,
                          NotificationService notificationService) { // NEW
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService; // NEW
    }

    // ---------- SEND ----------
    public RequestResponse sendRequest(SendExchangeRequest dto) {
        if (dto.getSenderId() == null || dto.getReceiverId() == null) {
            throw new IllegalArgumentException("Sender and receiver are required");
        }
        if (dto.getSenderId().equals(dto.getReceiverId())) {
            throw new IllegalArgumentException("You cannot send a request to yourself");
        }

        User sender = userRepository.findById(dto.getSenderId())
                .orElseThrow(() -> new IllegalArgumentException("Sender not found"));
        User receiver = userRepository.findById(dto.getReceiverId())
                .orElseThrow(() -> new IllegalArgumentException("Receiver not found"));

        if (dto.getMessage() == null || dto.getMessage().isBlank()) {
            throw new IllegalArgumentException("Message is required");
        }
        if (dto.getMessage().length() > 500) {
            throw new IllegalArgumentException("Message must be 500 characters or less");
        }

        // Already connected? (an ACCEPTED request exists in either direction)
        if (existsBetween(sender.getId(), receiver.getId(), RequestStatus.ACCEPTED)) {
            throw new IllegalArgumentException("You are already connected with this student");
        }

        // Already waiting for an answer? (a PENDING request exists in either direction)
        if (existsBetween(sender.getId(), receiver.getId(), RequestStatus.PENDING)) {
            throw new IllegalArgumentException(
                    "A request is already pending between you and this student");
        }

        ExchangeRequest request = new ExchangeRequest();
        request.setSender(sender);
        request.setReceiver(receiver);
        request.setMessage(dto.getMessage().trim());
        request.setStatus(RequestStatus.PENDING);
        request.setCreatedAt(LocalDateTime.now());

        ExchangeRequest saved = requestRepository.save(request);

        // NEW: tell the receiver
        notificationService.createNotification(
                receiver,
                sender.getName() + " sent you a skill exchange request.",
                NotificationType.NEW_REQUEST);

        return convertToResponse(saved);
    }

    // ---------- VIEW RECEIVED ----------
    public List<RequestResponse> getReceivedRequests(Long userId, RequestStatus status) {
        checkUserExists(userId);

        List<ExchangeRequest> requests;
        if (status == null) {
            requests = requestRepository.findByReceiverIdOrderByCreatedAtDesc(userId);
        } else {
            requests = requestRepository.findByReceiverIdAndStatusOrderByCreatedAtDesc(userId, status);
        }
        return convertList(requests);
    }

    // ---------- VIEW SENT ----------
    public List<RequestResponse> getSentRequests(Long userId) {
        checkUserExists(userId);
        return convertList(requestRepository.findBySenderIdOrderByCreatedAtDesc(userId));
    }

    // ---------- ACCEPT / REJECT ----------
    public RequestResponse acceptRequest(Long requestId, Long userId) {
        return respondToRequest(requestId, userId, RequestStatus.ACCEPTED);
    }

    public RequestResponse rejectRequest(Long requestId, Long userId) {
        return respondToRequest(requestId, userId, RequestStatus.REJECTED);
    }

    private RequestResponse respondToRequest(Long requestId, Long userId, RequestStatus newStatus) {
        ExchangeRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));

        // Only the receiver can answer
        if (!request.getReceiver().getId().equals(userId)) {
            throw new IllegalArgumentException("Only the receiver can accept or reject this request");
        }

        // A request can be answered only once
        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalArgumentException("This request is already " + request.getStatus());
        }

        request.setStatus(newStatus);
        ExchangeRequest saved = requestRepository.save(request);

        User sender = saved.getSender();
        User receiver = saved.getReceiver();

        // NEW: create the notifications
        if (newStatus == RequestStatus.ACCEPTED) {
            notificationService.createNotification(
                    sender,
                    receiver.getName() + " accepted your skill exchange request.",
                    NotificationType.REQUEST_ACCEPTED);

            notificationService.createNotification(
                    sender,
                    "You are now connected with " + receiver.getName() + ".",
                    NotificationType.NEW_CONNECTION);

            notificationService.createNotification(
                    receiver,
                    "You are now connected with " + sender.getName() + ".",
                    NotificationType.NEW_CONNECTION);
        } else {
            notificationService.createNotification(
                    sender,
                    receiver.getName() + " rejected your skill exchange request.",
                    NotificationType.REQUEST_REJECTED);
        }

        return convertToResponse(saved);
    }

    // ---------- HELPERS ----------
    // True if "first sent to second" OR "second sent to first" with this status
    private boolean existsBetween(Long firstId, Long secondId, RequestStatus status) {
        return requestRepository.existsBySenderIdAndReceiverIdAndStatus(firstId, secondId, status)
                || requestRepository.existsBySenderIdAndReceiverIdAndStatus(secondId, firstId, status);
    }

    private void checkUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User not found");
        }
    }

    private List<RequestResponse> convertList(List<ExchangeRequest> requests) {
        List<RequestResponse> responses = new ArrayList<>();
        for (ExchangeRequest request : requests) {
            responses.add(convertToResponse(request));
        }
        return responses;
    }

    private RequestResponse convertToResponse(ExchangeRequest request) {
        return new RequestResponse(
                request.getId(),
                request.getSender().getId(),
                request.getSender().getName(),
                request.getReceiver().getId(),
                request.getReceiver().getName(),
                request.getMessage(),
                request.getStatus(),
                request.getCreatedAt()
        );
    }
}