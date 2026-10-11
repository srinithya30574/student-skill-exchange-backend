package com.example.student_skill_exchange.controller;

import com.example.student_skill_exchange.dto.DashboardResponse;
import com.example.student_skill_exchange.dto.ErrorResponse;
import com.example.student_skill_exchange.service.DashboardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    // GET http://localhost:8080/api/dashboard/2
    @GetMapping("/{userId}")
    public ResponseEntity<?> getDashboard(@PathVariable Long userId) {
        try {
            DashboardResponse dashboard = dashboardService.getDashboard(userId);
            return ResponseEntity.ok(dashboard);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }
}