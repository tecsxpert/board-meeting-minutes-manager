import api from "./api";

export const getAllMeetings = (page = 0, size = 10) =>
    api.get(`/api/minutes?page=${page}&size=${size}`);

export const getMeetingById = (id) => api.get(`/api/minutes/${id}`);

export const createMeeting = (data) => api.post("/api/minutes", data);

export const updateMeeting = (id, data) => api.put(`/api/minutes/${id}`, data);

export const deleteMeeting = (id) => api.delete(`/api/minutes/${id}`);

export const searchMeetings = (query, page = 0, size = 10) =>
    api.get(`/api/minutes/search?q=${encodeURIComponent(query)}&page=${page}&size=${size}`);

export const getStats = () => api.get("/api/minutes/stats");