package com.example.student_skill_exchange.service;

import com.example.student_skill_exchange.dto.ConnectionResponse;
import com.example.student_skill_exchange.entity.ExchangeRequest;
import com.example.student_skill_exchange.entity.RequestStatus;
import com.example.student_skill_exchange.entity.SkillType;
import com.example.student_skill_exchange.entity.User;
import com.example.student_skill_exchange.entity.UserSkill;
import com.example.student_skill_exchange.repository.ExchangeRequestRepository;
import com.example.student_skill_exchange.repository.UserRepository;
import com.example.student_skill_exchange.repository.UserSkillRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ConnectionService {

    private final ExchangeRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final UserSkillRepository userSkillRepository;

    public ConnectionService(ExchangeRequestRepository requestRepository,
                             UserRepository userRepository,
                             UserSkillRepository userSkillRepository) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.userSkillRepository = userSkillRepository;
    }

    public List<ConnectionResponse> getConnections(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User not found");
        }

        // 1. Collect every ACCEPTED request where I am the sender or the receiver
        List<ExchangeRequest> acceptedRequests = new ArrayList<>();
        acceptedRequests.addAll(requestRepository.findBySenderIdAndStatus(userId, RequestStatus.ACCEPTED));
        acceptedRequests.addAll(requestRepository.findByReceiverIdAndStatus(userId, RequestStatus.ACCEPTED));

        // 2. For each one, find the OTHER student
        List<ConnectionResponse> connections = new ArrayList<>();
        for (ExchangeRequest request : acceptedRequests) {
            User other;
            if (request.getSender().getId().equals(userId)) {
                other = request.getReceiver();
            } else {
                other = request.getSender();
            }
            connections.add(convertToResponse(other, request));
        }

        // 3. Newest first
        connections.sort((a, b) -> b.getRequestedAt().compareTo(a.getRequestedAt()));
        return connections;
    }

    private ConnectionResponse convertToResponse(User other, ExchangeRequest request) {
        List<UserSkill> teach = userSkillRepository.findByUserIdAndSkillType(other.getId(), SkillType.TEACH);
        List<UserSkill> learn = userSkillRepository.findByUserIdAndSkillType(other.getId(), SkillType.LEARN);

        return new ConnectionResponse(
                other.getId(),
                other.getName(),
                other.getDepartment(),
                other.getYear(),
                other.getBio(),
                getSkillNames(teach),
                getSkillNames(learn),
                request.getId(),
                request.getCreatedAt()
        );
    }

    private List<String> getSkillNames(List<UserSkill> userSkills) {
        List<String> names = new ArrayList<>();
        for (UserSkill userSkill : userSkills) {
            names.add(userSkill.getSkill().getName());
        }
        return names;
    }
}