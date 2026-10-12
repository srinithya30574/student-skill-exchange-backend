import axios from "axios";

// Every request will start with this address
const api = axios.create({
  baseURL: "/api",
});

export default api;