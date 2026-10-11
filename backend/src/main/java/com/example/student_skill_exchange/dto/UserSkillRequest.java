package com.example.student_skill_exchange.dto;

import com.example.student_skill_exchange.entity.SkillCategory;
import com.example.student_skill_exchange.entity.SkillType;

public class UserSkillRequest {

    private String skillName;
    private SkillCategory category;
    private SkillType skillType;

    public UserSkillRequest() {
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public SkillCategory getCategory() {
        return category;
    }

    public void setCategory(SkillCategory category) {
        this.category = category;
    }

    public SkillType getSkillType() {
        return skillType;
    }

    public void setSkillType(SkillType skillType) {
        this.skillType = skillType;
    }
}
