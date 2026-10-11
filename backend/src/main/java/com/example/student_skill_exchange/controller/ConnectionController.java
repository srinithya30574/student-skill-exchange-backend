package com.example.student_skill_exchange.controller;

import com.example.student_skill_exchange.dto.ConnectionResponse;
import com.example.student_skill_exchange.dto.ErrorResponse;
import com.example.student_skill_exchange.service.ConnectionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/connections")
public class ConnectionController {

    private final ConnectionService connectionService;

    public ConnectionController(ConnectionService connectionService) {
        this.connectionService = connectionService;
    }

    // GET http://localhost:8080/api/connections/2
    @GetMapping("/{userId}")
    public ResponseEntity<?> getConnections(@PathVariable Long userId) {
        try {
            List<ConnectionResponse> connections = connectionService.getConnections(userId);
            return ResponseEntity.ok(connections);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }
}