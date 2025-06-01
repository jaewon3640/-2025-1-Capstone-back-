package com.example.demo.repository;

import com.example.demo.UserRole;
import com.example.demo.domain.Notification;
import com.example.demo.domain.schedule.ScheduleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByScheduleId(Long scheduleId);
    boolean existsByScheduleIdAndTitleAndTypeAndNotifiedAt(
            Long id, String title, ScheduleType type, LocalDateTime dateTime);
    void deleteByScheduleId(Long ScheduleId);

    boolean existsByCaregiverIdAndTitleAndTypeAndNotifiedAt(
            Long userId, String title, ScheduleType type, LocalDateTime notifiedAt);
    boolean existsByProtectedUserIdAndTitleAndTypeAndNotifiedAt(
            Long userId, String title, ScheduleType type, LocalDateTime notifiedAt);

    List<Notification> findByCaregiverId(Long userId);
    List<Notification> findByProtectedUserId(Long userId);
    List<Notification> findByProtectedUserIdAndRole(Long userId, UserRole userRole);
    List<Notification> findBySentAndNotifiedAtAfter(boolean sent, LocalDateTime now);
}