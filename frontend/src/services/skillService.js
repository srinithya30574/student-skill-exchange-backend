import api from "./api";

export function getUserSkills(userId) {
  return api.get(`/users/${userId}/skills`);
}

export function getCategories() {
  return api.get("/skills/categories");
}

export function getAllSkills() {
  return api.get("/skills");
}

export function addUserSkill(userId, data) {
  return api.post(`/users/${userId}/skills`, data);
}

export function updateUserSkill(userId, userSkillId, data) {
  return api.put(`/users/${userId}/skills/${userSkillId}`, data);
}

export function removeUserSkill(userId, userSkillId) {
  return api.delete(`/users/${userId}/skills/${userSkillId}`);
}