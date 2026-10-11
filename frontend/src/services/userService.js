import api from "./api";

export function getUserById(userId) {
  return api.get(`/users/${userId}`);
}

export function updateProfile(userId, data) {
  return api.put(`/users/${userId}`, data);
}