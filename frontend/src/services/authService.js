import api from "./api";

export const loginUser = (username, password) =>
    api.post("/api/auth/login", { username, password });

export const registerUser = (username, password, email) =>
    api.post("/api/auth/register", { username, password, email });