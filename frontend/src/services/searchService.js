import api from "./api";

export function searchStudents(params) {
  return api.get("/students/search", { params });
}