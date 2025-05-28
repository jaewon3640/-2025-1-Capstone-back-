package com.example.demo.repository;

import com.example.demo.domain.schedule.RecurringSchedule;
import com.example.demo.domain.schedule.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RecurringScheduleRepository extends JpaRepository<RecurringSchedule, Long> {
    List<RecurringSchedule> findByProtectedUserId(Long protectedUserId);
    void deleteByProtectedUserIdAndTitle(Long protectedUserId, String title);
}
