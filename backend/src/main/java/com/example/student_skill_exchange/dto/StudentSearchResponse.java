package com.example.student_skill_exchange.dto;

import java.util.List;

public class StudentSearchResponse {

    private Long id;
    private String name;
    private String department;
    private Integer year;
    private String bio;
    private List<String> teachSkills;
    private List<String> learnSkills;

    public StudentSearchResponse() {
    }

    public StudentSearchResponse(Long id, String name, String department,
                                 Integer year, String bio,
                                 List<String> teachSkills,
                                 List<String> learnSkills) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.year = year;
        this.bio = bio;
        this.teachSkills = teachSkills;
        this.learnSkills = learnSkills;
    }

    public Long getId() {
        return id;
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
}
