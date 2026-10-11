import api from "./api";

export function getNotifications(userId) {
  return api.get(`/notifications/user/${userId}`);
}

export function getUnreadCount(userId) {
  return api.get(`/notifications/user/${userId}/unread-count`);
}

export function markAsRead(notificationId, userId) {
  return api.put(`/notifications/${notificationId}/read`, null, { params: { userId } });
}

export function markAllAsRead(userId) {
  return api.put(`/notifications/user/${userId}/read-all`);
}