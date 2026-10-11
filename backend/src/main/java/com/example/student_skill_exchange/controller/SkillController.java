package com.example.student_skill_exchange.controller;

import com.example.student_skill_exchange.dto.ErrorResponse;
import com.example.student_skill_exchange.dto.UserSkillRequest;
import com.example.student_skill_exchange.dto.UserSkillResponse;
import com.example.student_skill_exchange.entity.Skill;
import com.example.student_skill_exchange.entity.SkillCategory;
import com.example.student_skill_exchange.entity.SkillType;
import com.example.student_skill_exchange.service.SkillService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SkillController {

    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    // GET http://localhost:8080/api/skills/categories
    @GetMapping("/skills/categories")
    public List<SkillCategory> getCategories() {
        return skillService.getCategories();
    }

    // GET http://localhost:8080/api/skills
    @GetMapping("/skills")
    public List<Skill> getAllSkills() {
        return skillService.getAllSkills();
    }

    // GET http://localhost:8080/api/users/2/skills
    // GET http://localhost:8080/api/users/2/skills?type=TEACH
    @GetMapping("/users/{userId}/skills")
    public ResponseEntity<?> getUserSkills(@PathVariable Long userId,
                                           @RequestParam(required = false) SkillType type) {
        try {
            List<UserSkillResponse> skills = skillService.getUserSkills(userId, type);
            return ResponseEntity.ok(skills);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    // POST http://localhost:8080/api/users/2/skills
    @PostMapping("/users/{userId}/skills")
    public ResponseEntity<?> addUserSkill(@PathVariable Long userId,
                                          @RequestBody UserSkillRequest request) {
        try {
            UserSkillResponse created = skillService.addUserSkill(userId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // PUT http://localhost:8080/api/users/2/skills/5
    @PutMapping("/users/{userId}/skills/{userSkillId}")
    public ResponseEntity<?> updateUserSkill(@PathVariable Long userId,
                                             @PathVariable Long userSkillId,
                                             @RequestBody UserSkillRequest request) {
        try {
            UserSkillResponse updated = skillService.updateUserSkill(userId, userSkillId, request);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // DELETE http://localhost:8080/api/users/2/skills/5
    @DeleteMapping("/users/{userId}/skills/{userSkillId}")
    public ResponseEntity<?> removeUserSkill(@PathVariable Long userId,
                                             @PathVariable Long userSkillId) {
        try {
            skillService.removeUserSkill(userId, userSkillId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }
}
