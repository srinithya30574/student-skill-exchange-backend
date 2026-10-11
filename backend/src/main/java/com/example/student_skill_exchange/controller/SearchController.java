package com.example.student_skill_exchange.controller;

import com.example.student_skill_exchange.dto.StudentSearchResponse;
import com.example.student_skill_exchange.entity.SkillCategory;
import com.example.student_skill_exchange.service.SearchService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    // GET http://localhost:8080/api/students/search?skill=React&department=CSE&year=2
    @GetMapping("/search")
    public List<StudentSearchResponse> searchStudents(
            @RequestParam(required = false) String skill,
            @RequestParam(required = false) SkillCategory category,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Long excludeUserId) {

        return searchService.searchStudents(skill, category, department, year, excludeUserId);
    }
}