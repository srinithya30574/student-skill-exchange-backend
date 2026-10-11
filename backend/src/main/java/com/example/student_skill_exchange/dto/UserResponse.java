package com.example.student_skill_exchange.dto;

public class UserResponse {

    private Long id;
    private String name;
    private String email;
    private String department;
    private Integer year;
    private String bio;

    public UserResponse() {
    }

    public UserResponse(Long id, String name, String email,
                        String department, Integer year, String bio) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.department = department;
        this.year = year;
        this.bio = bio;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
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
}
