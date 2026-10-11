import api from "./api";

export function getMatches(userId) {
  return api.get(`/matches/${userId}`);
}