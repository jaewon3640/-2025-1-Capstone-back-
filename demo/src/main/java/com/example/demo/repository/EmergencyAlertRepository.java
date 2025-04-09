package com.example.demo.repository;


import com.example.demo.domain.EmergencyAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface EmergencyAlertRepository extends JpaRepository<EmergencyAlert, Long> {
    List<EmergencyAlert> findByProtectedUserId(Long protectedUserId);

}