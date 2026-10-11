package com.example.student_skill_exchange.service;

import com.example.student_skill_exchange.dto.UserSkillRequest;
import com.example.student_skill_exchange.dto.UserSkillResponse;
import com.example.student_skill_exchange.entity.Skill;
import com.example.student_skill_exchange.entity.SkillCategory;
import com.example.student_skill_exchange.entity.SkillType;
import com.example.student_skill_exchange.entity.User;
import com.example.student_skill_exchange.entity.UserSkill;
import com.example.student_skill_exchange.repository.SkillRepository;
import com.example.student_skill_exchange.repository.UserRepository;
import com.example.student_skill_exchange.repository.UserSkillRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class SkillService {

    private final SkillRepository skillRepository;
    private final UserSkillRepository userSkillRepository;
    private final UserRepository userRepository;

    public SkillService(SkillRepository skillRepository,
                        UserSkillRepository userSkillRepository,
                        UserRepository userRepository) {
        this.skillRepository = skillRepository;
        this.userSkillRepository = userSkillRepository;
        this.userRepository = userRepository;
    }

    // ---------- CATEGORIES ----------
    public List<SkillCategory> getCategories() {
        return Arrays.asList(SkillCategory.values());
    }

    // ---------- ALL SKILLS IN THE SYSTEM ----------
    public List<Skill> getAllSkills() {
        return skillRepository.findAll();
    }

    // ---------- GET A USER'S SKILLS ----------
    public List<UserSkillResponse> getUserSkills(Long userId, SkillType type) {
        checkUserExists(userId);

        List<UserSkill> userSkills;
        if (type == null) {
            userSkills = userSkillRepository.findByUserId(userId);
        } else {
            userSkills = userSkillRepository.findByUserIdAndSkillType(userId, type);
        }

        List<UserSkillResponse> responses = new ArrayList<>();
        for (UserSkill userSkill : userSkills) {
            responses.add(convertToResponse(userSkill));
        }
        return responses;
    }

    // ---------- ADD ----------
    public UserSkillResponse addUserSkill(Long userId, UserSkillRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        validateRequest(request);

        Skill skill = findOrCreateSkill(request.getSkillName(), request.getCategory());

        if (userSkillRepository.existsByUserIdAndSkillIdAndSkillType(
                userId, skill.getId(), request.getSkillType())) {
            throw new IllegalArgumentException("You have already added this skill as "
                    + request.getSkillType());
        }

        UserSkill userSkill = new UserSkill();
        userSkill.setUser(user);
        userSkill.setSkill(skill);
        userSkill.setSkillType(request.getSkillType());

        return convertToResponse(userSkillRepository.save(userSkill));
    }

    // ---------- EDIT ----------
    public UserSkillResponse updateUserSkill(Long userId, Long userSkillId,
                                             UserSkillRequest request) {
        UserSkill userSkill = findOwnedUserSkill(userId, userSkillId);
        validateRequest(request);

        Skill skill = findOrCreateSkill(request.getSkillName(), request.getCategory());

        boolean somethingChanged =
                !userSkill.getSkill().getId().equals(skill.getId())
                        || userSkill.getSkillType() != request.getSkillType();

        if (somethingChanged && userSkillRepository.existsByUserIdAndSkillIdAndSkillType(
                userId, skill.getId(), request.getSkillType())) {
            throw new IllegalArgumentException("You have already added this skill as "
                    + request.getSkillType());
        }

        userSkill.setSkill(skill);
        userSkill.setSkillType(request.getSkillType());

        return convertToResponse(userSkillRepository.save(userSkill));
    }

    // ---------- REMOVE ----------
    public void removeUserSkill(Long userId, Long userSkillId) {
        UserSkill userSkill = findOwnedUserSkill(userId, userSkillId);
        userSkillRepository.delete(userSkill);
    }

    // ---------- HELPERS ----------
    private void checkUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User not found");
        }
    }

    private void validateRequest(UserSkillRequest request) {
        if (request.getSkillName() == null || request.getSkillName().isBlank()) {
            throw new IllegalArgumentException("Skill name is required");
        }
        if (request.getSkillType() == null) {
            throw new IllegalArgumentException("Skill type is required (TEACH or LEARN)");
        }
    }

    // Reuse the skill if it exists, otherwise create it
    private Skill findOrCreateSkill(String skillName, SkillCategory category) {
        String cleanName = skillName.trim();

        return skillRepository.findByNameIgnoreCase(cleanName)
                .orElseGet(() -> {
                    Skill newSkill = new Skill();
                    newSkill.setName(cleanName);
                    newSkill.setCategory(category != null ? category : SkillCategory.OTHER);
                    return skillRepository.save(newSkill);
                });
    }

    // Find a user_skills row and make sure it belongs to this user
    private UserSkill findOwnedUserSkill(Long userId, Long userSkillId) {
        UserSkill userSkill = userSkillRepository.findById(userSkillId)
                .orElseThrow(() -> new IllegalArgumentException("Skill entry not found"));

        if (!userSkill.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("This skill does not belong to this user");
        }
        return userSkill;
    }

    private UserSkillResponse convertToResponse(UserSkill userSkill) {
        return new UserSkillResponse(
                userSkill.getId(),
                userSkill.getSkill().getId(),
                userSkill.getSkill().getName(),
                userSkill.getSkill().getCategory(),
                userSkill.getSkillType()
        );
    }
}
