import api from "./api";

export function getDashboard(userId) {
  return api.get(`/dashboard/${userId}`);
}