package com.example.student_skill_exchange.service;

import com.example.student_skill_exchange.dto.LoginRequest;
import com.example.student_skill_exchange.dto.RegisterRequest;
import com.example.student_skill_exchange.dto.UpdateProfileRequest;
import com.example.student_skill_exchange.dto.UserResponse;
import com.example.student_skill_exchange.entity.User;
import com.example.student_skill_exchange.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ---------- REGISTER ----------
    public UserResponse register(RegisterRequest request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (request.getPassword() == null || request.getPassword().length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }

        validateYear(request.getYear());
        validateLengths(request.getName(), request.getBio());

        String email = request.getEmail().trim().toLowerCase();
        if (email.length() > 150) {
            throw new IllegalArgumentException("Email must be 150 characters or less");
        }

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email is already registered");
        }

        User user = new User();
        user.setName(request.getName().trim());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setDepartment(request.getDepartment());
        user.setYear(request.getYear());
        user.setBio(request.getBio());

        User savedUser = userRepository.save(user);
        return convertToResponse(savedUser);
    }

    // ---------- LOGIN ----------
    public UserResponse login(LoginRequest request) {
        if (request.getEmail() == null || request.getPassword() == null) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        String email = request.getEmail().trim().toLowerCase();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        return convertToResponse(user);
    }

    // ---------- GET USERS ----------
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserResponse> responses = new ArrayList<>();
        for (User user : users) {
            responses.add(convertToResponse(user));
        }
        return responses;
    }

    public Optional<UserResponse> getUserById(Long id) {
        return userRepository.findById(id).map(this::convertToResponse);
    }

    // ---------- UPDATE PROFILE ----------
    public UserResponse updateProfile(Long id, UpdateProfileRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (request.getName() == null || request.getName().isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }
        validateYear(request.getYear());

        user.setName(request.getName().trim());
        user.setDepartment(request.getDepartment());
        user.setYear(request.getYear());
        user.setBio(request.getBio());

        User savedUser = userRepository.save(user);
        return convertToResponse(savedUser);
    }

    // ---------- HELPERS ----------
    private void validateYear(Integer year) {
        if (year != null && (year < 1 || year > 4)) {
            throw new IllegalArgumentException("Year must be between 1 and 4");
        }
    }

    private void validateLengths(String name, String bio) {
        if (name != null && name.length() > 100) {
            throw new IllegalArgumentException("Name must be 100 characters or less");
        }

        if (bio != null && bio.length() > 500) {
            throw new IllegalArgumentException("Bio must be 500 characters or less");
        }
    }


    // Converts a User (database object) into a UserResponse (safe to send)
    private UserResponse convertToResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getDepartment(),
                user.getYear(),
                user.getBio()
        );
    }
}