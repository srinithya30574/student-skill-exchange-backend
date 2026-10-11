package com.example.student_skill_exchange.dto;

import com.example.student_skill_exchange.entity.RequestStatus;

import java.time.LocalDateTime;

public class RequestResponse {

    private Long id;
    private Long senderId;
    private String senderName;
    private Long receiverId;
    private String receiverName;
    private String message;
    private RequestStatus status;
    private LocalDateTime createdAt;

    public RequestResponse() {
    }

    public RequestResponse(Long id, Long senderId, String senderName,
                           Long receiverId, String receiverName,
                           String message, RequestStatus status,
                           LocalDateTime createdAt) {
        this.id = id;
        this.senderId = senderId;
        this.senderName = senderName;
        this.receiverId = receiverId;
        this.receiverName = receiverName;
        this.message = message;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Long getSenderId() {
        return senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public String getMessage() {
        return message;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}