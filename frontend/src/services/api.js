import axios from "axios";

// Every request will start with this address
const api = axios.create({
  baseURL: "http://localhost:8080/api",
});

export default api;