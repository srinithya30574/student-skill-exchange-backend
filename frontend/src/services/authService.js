import api from "./api";

export function registerStudent(data) {
  return api.post("/auth/register", data);
}

export function loginStudent(data) {
  return api.post("/auth/login", data);
}