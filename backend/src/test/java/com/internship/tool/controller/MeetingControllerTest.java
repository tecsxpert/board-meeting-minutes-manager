package com.internship.tool.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.internship.tool.entity.MeetingMinutes;
import com.internship.tool.service.MeetingMinutesService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class MeetingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MeetingMinutesService meetingMinutesService;

    // Helper to build a dummy meeting
    private MeetingMinutes dummyMeeting() {
        return MeetingMinutes.builder()
                .id(1L)
                .title("Q1 Board Meeting")
                .meetingDate(LocalDate.of(2026, 4, 14))
                .content("Discussed Q1 results.")
                .status("DRAFT")
                .isDeleted(false)
                .build();
    }

    // GET /api/minutes
    @Test
    @WithMockUser(roles = "USER")
    void getAllMinutes_returns200() throws Exception {
        when(meetingMinutesService.getAll(any(PageRequest.class)))
            .thenReturn(new PageImpl<>(List.of(dummyMeeting())));

        mockMvc.perform(get("/api/minutes?page=0&size=10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content[0].title").value("Q1 Board Meeting"));
    }

    // GET /api/minutes without token
    @Test
    void getAllMinutes_withoutToken_returns401() throws Exception {
        mockMvc.perform(get("/api/minutes"))
            .andExpect(status().isUnauthorized());
    }

    // GET /api/minutes/{id}
    @Test
    @WithMockUser(roles = "USER")
    void getMinutesById_returns200() throws Exception {
        when(meetingMinutesService.getById(1L)).thenReturn(dummyMeeting());

        mockMvc.perform(get("/api/minutes/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.title").value("Q1 Board Meeting"));
    }

    // POST /api/minutes
    @Test
    @WithMockUser(roles = "USER")
    void createMinutes_returns201() throws Exception {
        MeetingMinutes input = dummyMeeting();

        when(meetingMinutesService.create(any(MeetingMinutes.class)))
            .thenReturn(dummyMeeting());

        mockMvc.perform(post("/api/minutes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1));
    }
}
