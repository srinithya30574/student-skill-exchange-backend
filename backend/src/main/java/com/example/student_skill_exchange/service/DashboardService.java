package com.example.student_skill_exchange.service;

import com.example.student_skill_exchange.dto.DashboardResponse;
import com.example.student_skill_exchange.dto.MatchResponse;
import com.example.student_skill_exchange.dto.NotificationResponse;
import com.example.student_skill_exchange.dto.RatingSummaryResponse;
import com.example.student_skill_exchange.dto.UserResponse;
import com.example.student_skill_exchange.dto.UserSkillResponse;
import com.example.student_skill_exchange.entity.RequestStatus;
import com.example.student_skill_exchange.entity.SkillType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DashboardService {

    private final UserService userService;
    private final SkillService skillService;
    private final MatchService matchService;
    private final RequestService requestService;
    private final ConnectionService connectionService;
    private final NotificationService notificationService;
    private final RatingService ratingService;

    public DashboardService(UserService userService,
                            SkillService skillService,
                            MatchService matchService,
                            RequestService requestService,
                            ConnectionService connectionService,
                            NotificationService notificationService,
                            RatingService ratingService) {
        this.userService = userService;
        this.skillService = skillService;
        this.matchService = matchService;
        this.requestService = requestService;
        this.connectionService = connectionService;
        this.notificationService = notificationService;
        this.ratingService = ratingService;
    }

    public DashboardResponse getDashboard(Long userId) {
        UserResponse user = userService.getUserById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<UserSkillResponse> teachSkills = skillService.getUserSkills(userId, SkillType.TEACH);
        List<UserSkillResponse> learnSkills = skillService.getUserSkills(userId, SkillType.LEARN);

        // Matches: count all, recommend the top 3
        List<MatchResponse> matches = matchService.getMatches(userId);
        List<MatchResponse> recommended = new ArrayList<>();
        for (int i = 0; i < matches.size() && i < 3; i++) {
            recommended.add(matches.get(i));
        }

        int pendingRequests =
                requestService.getReceivedRequests(userId, RequestStatus.PENDING).size();
        int connectionCount = connectionService.getConnections(userId).size();
        long unread = notificationService.getUnreadCount(userId);

        // Notifications: show the latest 5
        List<NotificationResponse> allNotifications = notificationService.getNotifications(userId);
        List<NotificationResponse> recent = new ArrayList<>();
        for (int i = 0; i < allNotifications.size() && i < 5; i++) {
            recent.add(allNotifications.get(i));
        }

        RatingSummaryResponse rating = ratingService.getSummary(userId);

        return new DashboardResponse(
                user.getId(),
                user.getName(),
                teachSkills,
                learnSkills,
                matches.size(),
                pendingRequests,
                connectionCount,
                unread,
                recent,
                recommended,
                rating.getAverageRating(),
                rating.getTotalRatings()
        );
    }
}