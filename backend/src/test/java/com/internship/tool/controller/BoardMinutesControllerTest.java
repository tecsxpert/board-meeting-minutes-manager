// BoardMinutesControllerTest.java: @WebMvcTest integration tests for BoardMinutesController.
// Uses @MockBean for service and tests all CRUD endpoints plus auth, validation, and 404 cases.

package com.internship.tool.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.internship.tool.dto.BoardMinutesDto;
import com.internship.tool.dto.BoardMinutesResponseDto;
import com.internship.tool.dto.StatsDto;
import com.internship.tool.entity.BoardMinutes;
import com.internship.tool.exception.ResourceNotFoundException;
import com.internship.tool.service.BoardMinutesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BoardMinutesController.class)
@ActiveProfiles("test")
class BoardMinutesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BoardMinutesService service;

    private ObjectMapper objectMapper;
    private UUID sampleId;
    private BoardMinutesResponseDto sampleResponse;
    private BoardMinutesDto sampleRequest;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        sampleId = UUID.randomUUID();

        sampleResponse = BoardMinutesResponseDto.builder()
                .id(sampleId)
                .title("Q1 2026 Strategy Review")
                .meetingDate(LocalDate.of(2026, 1, 15))
                .attendees("CEO, CFO, CTO")
                .content("Reviewed Q1 performance metrics and strategic goals.")
                .status(BoardMinutes.Status.PUBLISHED.name())
                .aiDescription("A strategic quarterly review.")
                .aiRecommendations("1. Expand APAC presence.\n2. Review cloud vendor.")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        sampleRequest = BoardMinutesDto.builder()
                .title("Q1 2026 Strategy Review")
                .meetingDate(LocalDate.of(2026, 1, 15))
                .attendees("CEO, CFO, CTO")
                .content("Reviewed Q1 performance metrics.")
                .status(BoardMinutes.Status.PUBLISHED)
                .build();
    }

    // ─── GET /api/minutes → 200 ──────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/minutes → 200 with paginated list")
    @WithMockUser
    void getAllMinutes_returns200() throws Exception {
        when(service.getAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(sampleResponse)));

        mockMvc.perform(get("/api/minutes")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Q1 2026 Strategy Review"));
    }

    // ─── GET /api/minutes/{id} with valid id → 200 ───────────────────────────

    @Test
    @DisplayName("GET /api/minutes/{id} with valid ID → 200")
    @WithMockUser
    void getById_validId_returns200() throws Exception {
        when(service.getById(sampleId)).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/minutes/{id}", sampleId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sampleId.toString()))
                .andExpect(jsonPath("$.title").value("Q1 2026 Strategy Review"));
    }

    // ─── GET /api/minutes/{id} with missing id → 404 ─────────────────────────

    @Test
    @DisplayName("GET /api/minutes/{id} with non-existent ID → 404")
    @WithMockUser
    void getById_unknownId_returns404() throws Exception {
        UUID unknownId = UUID.randomUUID();
        when(service.getById(unknownId))
                .thenThrow(new ResourceNotFoundException("BoardMinutes", "id", unknownId));

        mockMvc.perform(get("/api/minutes/{id}", unknownId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    // ─── POST /api/minutes with valid body → 201 ─────────────────────────────

    @Test
    @DisplayName("POST /api/minutes with valid body → 201 Created")
    @WithMockUser
    void create_validBody_returns201() throws Exception {
        when(service.create(any(BoardMinutesDto.class))).thenReturn(sampleResponse);

        mockMvc.perform(post("/api/minutes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Q1 2026 Strategy Review"));
    }

    // ─── POST /api/minutes with blank title → 400 ─────────────────────────────

    @Test
    @DisplayName("POST /api/minutes with blank title → 400 Bad Request")
    @WithMockUser
    void create_blankTitle_returns400() throws Exception {
        BoardMinutesDto invalidDto = BoardMinutesDto.builder()
                .title("")   // blank — should fail @NotBlank
                .meetingDate(LocalDate.of(2026, 3, 1))
                .build();

        mockMvc.perform(post("/api/minutes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.title").exists());
    }

    // ─── PUT /api/minutes/{id} → 200 ─────────────────────────────────────────

    @Test
    @DisplayName("PUT /api/minutes/{id} with valid body → 200 OK")
    @WithMockUser
    void update_validBody_returns200() throws Exception {
        when(service.update(eq(sampleId), any(BoardMinutesDto.class))).thenReturn(sampleResponse);

        mockMvc.perform(put("/api/minutes/{id}", sampleId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sampleId.toString()));
    }

    // ─── DELETE /api/minutes/{id} → 204 ──────────────────────────────────────

    @Test
    @DisplayName("DELETE /api/minutes/{id} → 204 No Content")
    @WithMockUser
    void delete_existingId_returns204() throws Exception {
        doNothing().when(service).softDelete(sampleId);

        mockMvc.perform(delete("/api/minutes/{id}", sampleId)
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    // ─── GET /api/minutes without Authorization header → 401 ─────────────────

    @Test
    @DisplayName("GET /api/minutes without Authorization header → 401 Unauthorized")
    void getAll_noAuth_returns401() throws Exception {
        mockMvc.perform(get("/api/minutes"))
                .andExpect(status().isUnauthorized());
    }

    // ─── GET /api/minutes/stats → 200 ────────────────────────────────────────

    @Test
    @DisplayName("GET /api/minutes/stats → 200 with KPI counts")
    @WithMockUser
    void getStats_returns200() throws Exception {
        StatsDto stats = new StatsDto(15L, 6L, 5L, 4L);
        when(service.getStats()).thenReturn(stats);

        mockMvc.perform(get("/api/minutes/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(15))
                .andExpect(jsonPath("$.published").value(6))
                .andExpect(jsonPath("$.draft").value(5))
                .andExpect(jsonPath("$.archived").value(4));
    }
}
