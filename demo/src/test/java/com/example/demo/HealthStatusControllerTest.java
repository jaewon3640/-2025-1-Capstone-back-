package com.example.demo.controller;

import com.example.demo.DTO.HealthStatusRequest;
import com.example.demo.DTO.HealthStatusResponse;
import com.example.demo.MoodStatus;
import com.example.demo.service.HealthStatusService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class HealthStatusControllerTest {

    private MockMvc mockMvc;

    @Mock
    private HealthStatusService healthStatusService;

    @InjectMocks
    private HealthStatusController healthStatusController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(healthStatusController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void createHealthStatus_shouldReturnHealthStatusResponse() throws Exception {
        // Given
        Long userId = 1L;
        HealthStatusRequest request = new HealthStatusRequest(
                true,
                false,
                MoodStatus.GOOD,
                LocalDateTime.now(),
                userId
        );
        HealthStatusResponse response = new HealthStatusResponse(
                1L,
                true,
                false,
                "GOOD",
                LocalDateTime.now(),
                userId,
                "테스트유저"
        );

        when(healthStatusService.saveFromDto(request)).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/healthstatus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.checkMedicine").value(true))
                .andExpect(jsonPath("$.checkBreakfast").value(false))
                .andExpect(jsonPath("$.mood").value("GOOD"))
                .andExpect(jsonPath("$.protectedUserId").value(userId))
                .andExpect(jsonPath("$.protectedUserName").value("테스트유저"));

        verify(healthStatusService).saveFromDto(request);
    }

    @Test
    void getHealthStatusByUserId_shouldReturnListOfHealthStatusResponse() throws Exception {
        // Given
        Long protectedUserId = 1L;
        HealthStatusResponse response1 = new HealthStatusResponse(
                1L,
                true,
                false,
                "GOOD",
                LocalDateTime.now(),
                protectedUserId,
                "테스트유저1"
        );
        HealthStatusResponse response2 = new HealthStatusResponse(
                2L,
                false,
                true,
                "SAD",
                LocalDateTime.now(),
                protectedUserId,
                "테스트유저2"
        );
        List<HealthStatusResponse> responses = Arrays.asList(response1, response2);

        when(healthStatusService.getByUserId(protectedUserId)).thenReturn(responses);

        // When & Then
        mockMvc.perform(get("/api/healthstatus/protected/{protectedUserId}", protectedUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[0].mood").value("GOOD"))
                .andExpect(jsonPath("$[1].mood").value("SAD"));

        verify(healthStatusService).getByUserId(protectedUserId);
    }
}
