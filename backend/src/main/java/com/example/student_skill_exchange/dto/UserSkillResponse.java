package com.example.student_skill_exchange.dto;

import com.example.student_skill_exchange.entity.SkillCategory;
import com.example.student_skill_exchange.entity.SkillType;

public class UserSkillResponse {

    private Long id;
    private Long skillId;
    private String skillName;
    private SkillCategory category;
    private SkillType skillType;

    public UserSkillResponse() {
    }

    public UserSkillResponse(Long id, Long skillId, String skillName,
                             SkillCategory category, SkillType skillType) {
        this.id = id;
        this.skillId = skillId;
        this.skillName = skillName;
        this.category = category;
        this.skillType = skillType;
    }

    public Long getId() {
        return id;
    }

    public Long getSkillId() {
        return skillId;
    }

    public String getSkillName() {
        return skillName;
    }

    public SkillCategory getCategory() {
        return category;
    }

    public SkillType getSkillType() {
        return skillType;
    }
}
