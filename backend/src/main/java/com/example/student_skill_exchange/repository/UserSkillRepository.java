
package com.example.student_skill_exchange.repository;

import com.example.student_skill_exchange.entity.SkillType;
import com.example.student_skill_exchange.entity.UserSkill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserSkillRepository extends JpaRepository<UserSkill, Long> {

    List<UserSkill> findByUserId(Long userId);

    List<UserSkill> findByUserIdAndSkillType(Long userId, SkillType skillType);

    boolean existsByUserIdAndSkillIdAndSkillType(Long userId, Long skillId, SkillType skillType);
}