package com.example.student_skill_exchange.service;

import com.example.student_skill_exchange.dto.MatchResponse;
import com.example.student_skill_exchange.entity.SkillType;
import com.example.student_skill_exchange.entity.User;
import com.example.student_skill_exchange.entity.UserSkill;
import com.example.student_skill_exchange.repository.UserRepository;
import com.example.student_skill_exchange.repository.UserSkillRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class MatchService {

    private final UserRepository userRepository;
    private final UserSkillRepository userSkillRepository;

    public MatchService(UserRepository userRepository,
                        UserSkillRepository userSkillRepository) {
        this.userRepository = userRepository;
        this.userSkillRepository = userSkillRepository;
    }

    // ---------- ALL MATCHES FOR ONE STUDENT (ranked) ----------
    public List<MatchResponse> getMatches(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User not found");
        }

        // Load my skills once
        List<UserSkill> myTeach = userSkillRepository.findByUserIdAndSkillType(userId, SkillType.TEACH);
        List<UserSkill> myLearn = userSkillRepository.findByUserIdAndSkillType(userId, SkillType.LEARN);

        List<MatchResponse> matches = new ArrayList<>();

        for (User other : userRepository.findAll()) {
            if (other.getId().equals(userId)) {
                continue; // don't match with myself
            }

            MatchResponse match = calculateMatch(myTeach, myLearn, other);

            if (match.getMatchScore() > 0) {
                matches.add(match);
            }
        }

        // Highest score first
        matches.sort((a, b) -> Integer.compare(b.getMatchScore(), a.getMatchScore()));
        return matches;
    }

    // ---------- MATCH BETWEEN TWO SPECIFIC STUDENTS ----------
    public MatchResponse getMatchWith(Long userId, Long otherUserId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User not found");
        }
        User other = userRepository.findById(otherUserId)
                .orElseThrow(() -> new IllegalArgumentException("Other user not found"));

        if (userId.equals(otherUserId)) {
            throw new IllegalArgumentException("You cannot match with yourself");
        }

        List<UserSkill> myTeach = userSkillRepository.findByUserIdAndSkillType(userId, SkillType.TEACH);
        List<UserSkill> myLearn = userSkillRepository.findByUserIdAndSkillType(userId, SkillType.LEARN);

        return calculateMatch(myTeach, myLearn, other);
    }

    // ---------- THE MATCHING ALGORITHM ----------
    private MatchResponse calculateMatch(List<UserSkill> myTeach,
                                         List<UserSkill> myLearn,
                                         User other) {

        List<UserSkill> theirTeach = userSkillRepository.findByUserIdAndSkillType(other.getId(), SkillType.TEACH);
        List<UserSkill> theirLearn = userSkillRepository.findByUserIdAndSkillType(other.getId(), SkillType.LEARN);

        Set<Long> myTeachIds = getSkillIds(myTeach);
        Set<Long> theirTeachIds = getSkillIds(theirTeach);

        // Question 1: skills I want to learn that THEY can teach
        List<String> theyCanTeachYou = new ArrayList<>();
        for (UserSkill wanted : myLearn) {
            if (theirTeachIds.contains(wanted.getSkill().getId())) {
                theyCanTeachYou.add(wanted.getSkill().getName());
            }
        }

        // Question 2: skills THEY want to learn that I can teach
        List<String> youCanTeachThem = new ArrayList<>();
        for (UserSkill wanted : theirLearn) {
            if (myTeachIds.contains(wanted.getSkill().getId())) {
                youCanTeachThem.add(wanted.getSkill().getName());
            }
        }

        // The formula
        int totalWanted = myLearn.size() + theirLearn.size();
        int totalCovered = theyCanTeachYou.size() + youCanTeachThem.size();

        int score = 0;
        if (totalWanted > 0) {
            score = Math.round(totalCovered * 100f / totalWanted);
        }

        return new MatchResponse(
                other.getId(),
                other.getName(),
                other.getDepartment(),
                other.getYear(),
                score,
                getMatchLevel(score),
                theyCanTeachYou,
                youCanTeachThem
        );
    }

    // ---------- HELPERS ----------
    private Set<Long> getSkillIds(List<UserSkill> userSkills) {
        Set<Long> ids = new HashSet<>();
        for (UserSkill userSkill : userSkills) {
            ids.add(userSkill.getSkill().getId());
        }
        return ids;
    }

    private String getMatchLevel(int score) {
        if (score >= 100) {
            return "Excellent Match";
        } else if (score >= 75) {
            return "Good Match";
        } else if (score >= 50) {
            return "Partial Match";
        } else {
            return "Low Match";
        }
    }
}