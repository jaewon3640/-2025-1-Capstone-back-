package com.example.demo.repository;


import com.example.demo.domain.schedule.Schedule;
import com.example.demo.domain.schedule.ScheduleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByProtectedUserId(Long protectedUserId);
    List<Schedule> findByProtectedUserIdAndRecurringFalse(Long protectedUserId);
    List<Schedule> findByProtectedUserIdAndDateTimeBetween(Long protectedUserId, LocalDateTime start, LocalDateTime end);
    void deleteByProtectedUserIdAndTitle(Long protectedUserId, String title);

    @Query("SELECT DISTINCT s.title FROM Schedule s WHERE s.protectedUserId = :protectedUserId AND s.type = :type")
    List<String> findDistinctTitleByProtectedUserIdAndType(Long protectedUserId, ScheduleType type);

    Optional<Schedule> findByProtectedUserIdAndTitle(Long protectedUserId, String title);

}