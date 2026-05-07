import api from "./api";

export const getAllMeetings = (page = 0, size = 10) =>
    api.get(`/minutes?page=${page}&size=${size}`);

export const getMeetingById = (id) => api.get(`/minutes/${id}`);

export const createMeeting = (data) => api.post("/minutes/create", data);

export const updateMeeting = (id, data) => api.put(`/minutes/${id}`, data);

export const deleteMeeting = (id) => api.delete(`/minutes/${id}`);

export const searchMeetings = (query, page = 0, size = 10) =>
    api.get(`/minutes/search?q=${encodeURIComponent(query)}&page=${page}&size=${size}`);

export const getStats = () => api.get("/api/meetings/stats");

