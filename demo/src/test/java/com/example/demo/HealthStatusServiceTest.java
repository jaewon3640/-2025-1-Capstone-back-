package com.example.demo;

import com.example.demo.DTO.HealthStatusRequest;
import com.example.demo.DTO.HealthStatusResponse;
import com.example.demo.MoodStatus;
import com.example.demo.domain.HealthStatus;
import com.example.demo.domain.User;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.repository.HealthStatusRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.HealthStatusService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HealthStatusServiceTest {

    private HealthStatusRepository healthStatusRepository;
    private UserRepository userRepository;
    private HealthStatusService healthStatusService;

    @BeforeEach
    void setUp() {
        healthStatusRepository = mock(HealthStatusRepository.class);
        userRepository = mock(UserRepository.class);
        healthStatusService = new HealthStatusService(healthStatusRepository, userRepository);
    }

    @Test
    void saveFromDto_shouldSaveAndReturnHealthStatusResponse_whenUserExists() {
        // Given
        Long userId = 1L;
        User user = User.builder()
                .id(userId)
                .name("테스트유저")
                .email("test@example.com")
                .role(UserRole.보호자) // 수정된 enum 값 사용
                .build();

        HealthStatusRequest request = new HealthStatusRequest(
                true,
                false,
                MoodStatus.GOOD, // 변경된 값 확인 (HAPPY -> GOOD)
                LocalDateTime.of(2025, 5, 4, 12, 0),
                userId
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(healthStatusRepository.save(any(HealthStatus.class))).thenAnswer(invocation -> {
            HealthStatus status = invocation.getArgument(0);
            status.setId(100L); // 가짜 ID 부여
            return status;
        });

        // When
        HealthStatusResponse response = healthStatusService.saveFromDto(request);

        // Then
        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals(true, response.isCheckMedicine());
        assertEquals(false, response.isCheckBreakfast());
        assertEquals("GOOD", response.getMood());  // 실제 저장된 값 (HAPPY -> GOOD 확인)
        assertEquals(userId, response.getProtectedUserId());
        assertEquals("테스트유저", response.getProtectedUserName());

        verify(userRepository).findById(userId);
        verify(healthStatusRepository).save(any(HealthStatus.class));
    }


    @Test
    void saveFromDto_shouldThrowException_whenUserNotFound() {
        // Given
        Long invalidUserId = 999L;
        HealthStatusRequest request = new HealthStatusRequest(
                true,
                true,
                MoodStatus.SAD,
                LocalDateTime.now(),
                invalidUserId
        );

        when(userRepository.findById(invalidUserId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UserNotFoundException.class, () -> {
            healthStatusService.saveFromDto(request);
        });

        verify(userRepository).findById(invalidUserId);
        verify(healthStatusRepository, never()).save(any());
    }
}
