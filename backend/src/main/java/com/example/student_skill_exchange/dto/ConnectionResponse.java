package com.example.student_skill_exchange.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ConnectionResponse {

    private Long userId;
    private String name;
    private String department;
    private Integer year;
    private String bio;
    private List<String> teachSkills;
    private List<String> learnSkills;
    private Long requestId;
    private LocalDateTime requestedAt;

    public ConnectionResponse() {
    }

    public ConnectionResponse(Long userId, String name, String department,
                              Integer year, String bio,
                              List<String> teachSkills, List<String> learnSkills,
                              Long requestId, LocalDateTime requestedAt) {
        this.userId = userId;
        this.name = name;
        this.department = department;
        this.year = year;
        this.bio = bio;
        this.teachSkills = teachSkills;
        this.learnSkills = learnSkills;
        this.requestId = requestId;
        this.requestedAt = requestedAt;
    }

    public Long getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getDepartment() {
        return department;
    }

    public Integer getYear() {
        return year;
    }

    public String getBio() {
        return bio;
    }

    public List<String> getTeachSkills() {
        return teachSkills;
    }

    public List<String> getLearnSkills() {
        return learnSkills;
    }

    public Long getRequestId() {
        return requestId;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }
}