package com.internship.tool.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.internship.tool.config.JwtUtil;
import com.internship.tool.entity.User;
import com.internship.tool.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtUtil jwtUtil;

    // Test 1 — Register success
    @Test
    void register_ShouldReturn200_WhenValidRequest() throws Exception {
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@test.com")).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("hashedpassword");
        when(userRepository.save(any(User.class))).thenReturn(new User());

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        Map.of("username", "newuser", "email", "new@test.com", "password", "pass123"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully"));
    }

    // Test 2 — Register fails when username exists
    @Test
    void register_ShouldReturn400_WhenUsernameExists() throws Exception {
        when(userRepository.existsByUsername("existinguser")).thenReturn(true);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        Map.of("username", "existinguser", "email", "new@test.com", "password", "pass123"))))
                .andExpect(status().isBadRequest());
    }

    // Test 3 — Login success
    @Test
    void login_ShouldReturn200_WithToken_WhenValidCredentials() throws Exception {
        User user = User.builder()
                .username("testuser")
                .email("test@test.com")
                .password("hashedpassword")
                .role("USER")
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "hashedpassword")).thenReturn(true);
        when(jwtUtil.generateToken("testuser", "USER")).thenReturn("mock.jwt.token");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        Map.of("username", "testuser", "password", "password123"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mock.jwt.token"));
    }

    // Test 4 — Login fails with wrong password
    @Test
    void login_ShouldReturn400_WhenWrongPassword() throws Exception {
        User user = User.builder()
                .username("testuser")
                .password("hashedpassword")
                .role("USER")
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpassword", "hashedpassword")).thenReturn(false);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        Map.of("username", "testuser", "password", "wrongpassword"))))
                .andExpect(status().isBadRequest());
    }

    // Test 5 — Login fails when user not found
    @Test
    void login_ShouldReturn400_WhenUserNotFound() throws Exception {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        Map.of("username", "unknown", "password", "password123"))))
                .andExpect(status().isBadRequest());
    }

    // Test 6 — Register fails when missing fields
    @Test
    void register_ShouldReturn400_WhenMissingFields() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        Map.of("username", "newuser"))))
                .andExpect(status().isBadRequest());
    }
}
