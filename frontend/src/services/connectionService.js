import api from "./api";

export function getConnections(userId) {
  return api.get(`/connections/${userId}`);
}