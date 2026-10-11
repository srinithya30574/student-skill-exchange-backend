package com.example.student_skill_exchange.dto;

import java.util.List;

public class DashboardResponse {

    private Long userId;
    private String name;
    private List<UserSkillResponse> teachSkills;
    private List<UserSkillResponse> learnSkills;
    private int matchCount;
    private int pendingRequests;
    private int connectionCount;
    private long unreadNotifications;
    private List<NotificationResponse> recentNotifications;
    private List<MatchResponse> recommendedMatches;
    private double averageRating;
    private int totalRatings;

    public DashboardResponse() {
    }

    public DashboardResponse(Long userId, String name,
                             List<UserSkillResponse> teachSkills,
                             List<UserSkillResponse> learnSkills,
                             int matchCount, int pendingRequests,
                             int connectionCount, long unreadNotifications,
                             List<NotificationResponse> recentNotifications,
                             List<MatchResponse> recommendedMatches,
                             double averageRating, int totalRatings) {
        this.userId = userId;
        this.name = name;
        this.teachSkills = teachSkills;
        this.learnSkills = learnSkills;
        this.matchCount = matchCount;
        this.pendingRequests = pendingRequests;
        this.connectionCount = connectionCount;
        this.unreadNotifications = unreadNotifications;
        this.recentNotifications = recentNotifications;
        this.recommendedMatches = recommendedMatches;
        this.averageRating = averageRating;
        this.totalRatings = totalRatings;
    }

    public Long getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public List<UserSkillResponse> getTeachSkills() {
        return teachSkills;
    }

    public List<UserSkillResponse> getLearnSkills() {
        return learnSkills;
    }

    public int getMatchCount() {
        return matchCount;
    }

    public int getPendingRequests() {
        return pendingRequests;
    }

    public int getConnectionCount() {
        return connectionCount;
    }

    public long getUnreadNotifications() {
        return unreadNotifications;
    }

    public List<NotificationResponse> getRecentNotifications() {
        return recentNotifications;
    }

    public List<MatchResponse> getRecommendedMatches() {
        return recommendedMatches;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public int getTotalRatings() {
        return totalRatings;
    }
}