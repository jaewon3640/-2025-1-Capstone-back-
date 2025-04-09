package com.example.demo.repository;


import com.example.demo.domain.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByProtectedUserId(Long protectedUserId);
}