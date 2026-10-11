package com.example.student_skill_exchange.dto;

import java.util.List;

public class MatchResponse {

    private Long userId;
    private String name;
    private String department;
    private Integer year;
    private int matchScore;
    private String matchLevel;
    private List<String> theyCanTeachYou;
    private List<String> youCanTeachThem;

    public MatchResponse() {
    }

    public MatchResponse(Long userId, String name, String department, Integer year,
                         int matchScore, String matchLevel,
                         List<String> theyCanTeachYou, List<String> youCanTeachThem) {
        this.userId = userId;
        this.name = name;
        this.department = department;
        this.year = year;
        this.matchScore = matchScore;
        this.matchLevel = matchLevel;
        this.theyCanTeachYou = theyCanTeachYou;
        this.youCanTeachThem = youCanTeachThem;
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

    public int getMatchScore() {
        return matchScore;
    }

    public String getMatchLevel() {
        return matchLevel;
    }

    public List<String> getTheyCanTeachYou() {
        return theyCanTeachYou;
    }

    public List<String> getYouCanTeachThem() {
        return youCanTeachThem;
    }
}
