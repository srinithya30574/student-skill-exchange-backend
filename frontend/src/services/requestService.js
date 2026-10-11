import api from "./api";

export function sendRequest(data) {
  return api.post("/requests", data);
}

export function getSentRequests(userId) {
  return api.get(`/requests/sent/${userId}`);
}

export function getReceivedRequests(userId, status) {
  const params = status ? { status } : {};
  return api.get(`/requests/received/${userId}`, { params });
}

export function acceptRequest(requestId, userId) {
  return api.put(`/requests/${requestId}/accept`, null, { params: { userId } });
}

export function rejectRequest(requestId, userId) {
  return api.put(`/requests/${requestId}/reject`, null, { params: { userId } });
}