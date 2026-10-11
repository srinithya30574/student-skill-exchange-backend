package com.example.student_skill_exchange.service;

import com.example.student_skill_exchange.dto.StudentSearchResponse;
import com.example.student_skill_exchange.entity.SkillCategory;
import com.example.student_skill_exchange.entity.SkillType;
import com.example.student_skill_exchange.entity.User;
import com.example.student_skill_exchange.entity.UserSkill;
import com.example.student_skill_exchange.repository.UserRepository;
import com.example.student_skill_exchange.repository.UserSkillRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SearchService {

    private final UserRepository userRepository;
    private final UserSkillRepository userSkillRepository;

    public SearchService(UserRepository userRepository,
                         UserSkillRepository userSkillRepository) {
        this.userRepository = userRepository;
        this.userSkillRepository = userSkillRepository;
    }

    public List<StudentSearchResponse> searchStudents(String skill,
                                                      SkillCategory category,
                                                      String department,
                                                      Integer year,
                                                      Long excludeUserId) {

        List<StudentSearchResponse> results = new ArrayList<>();

        for (User user : userRepository.findAll()) {

            // 1. Skip the logged-in student
            if (excludeUserId != null && user.getId().equals(excludeUserId)) {
                continue;
            }

            // 2. Department and year filters
            if (!matchesDepartment(user, department)) {
                continue;
            }
            if (!matchesYear(user, year)) {
                continue;
            }

            // 3. Skill and category filters (look at this student's TEACH skills)
            List<UserSkill> teachSkills =
                    userSkillRepository.findByUserIdAndSkillType(user.getId(), SkillType.TEACH);

            if (!matchesSkillFilters(teachSkills, skill, category)) {
                continue;
            }

            // 4. This student passed every filter, so add them to the results
            List<UserSkill> learnSkills =
                    userSkillRepository.findByUserIdAndSkillType(user.getId(), SkillType.LEARN);

            results.add(new StudentSearchResponse(
                    user.getId(),
                    user.getName(),
                    user.getDepartment(),
                    user.getYear(),
                    user.getBio(),
                    getSkillNames(teachSkills),
                    getSkillNames(learnSkills)
            ));
        }

        return results;
    }

    // ---------- HELPERS ----------
    private boolean matchesDepartment(User user, String department) {
        if (department == null || department.isBlank()) {
            return true; // no department filter
        }
        return user.getDepartment() != null
                && user.getDepartment().trim().equalsIgnoreCase(department.trim());
    }

    private boolean matchesYear(User user, Integer year) {
        if (year == null) {
            return true; // no year filter
        }
        return year.equals(user.getYear());
    }

    private boolean matchesSkillFilters(List<UserSkill> teachSkills,
                                        String skill,
                                        SkillCategory category) {
        boolean noSkillFilter = (skill == null || skill.isBlank());
        boolean noCategoryFilter = (category == null);

        if (noSkillFilter && noCategoryFilter) {
            return true; // nothing to check
        }

        for (UserSkill userSkill : teachSkills) {
            boolean nameMatches = noSkillFilter
                    || userSkill.getSkill().getName().toLowerCase()
                    .contains(skill.trim().toLowerCase());

            boolean categoryMatches = noCategoryFilter
                    || userSkill.getSkill().getCategory() == category;

            if (nameMatches && categoryMatches) {
                return true; // this one skill satisfies both filters
            }
        }
        return false;
    }

    private List<String> getSkillNames(List<UserSkill> userSkills) {
        List<String> names = new ArrayList<>();
        for (UserSkill userSkill : userSkills) {
            names.add(userSkill.getSkill().getName());
        }
        return names;
    }
}
