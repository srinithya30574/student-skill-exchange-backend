import api from "./api";

export function getRatingSummary(userId) {
  return api.get(`/ratings/user/${userId}/summary`);
}

export function getRatingsReceived(userId) {
  return api.get(`/ratings/user/${userId}`);
}

export function getRatingsGiven(userId) {
  return api.get(`/ratings/given/${userId}`);
}

export function submitRating(data) {
  return api.post("/ratings", data);
}