package com.example.demo.repository;


import com.example.demo.domain.HealthStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface HealthStatusRepository extends JpaRepository<HealthStatus, Long> {
    List<HealthStatus> findByProtectedUserId(Long protectedUserId);
}