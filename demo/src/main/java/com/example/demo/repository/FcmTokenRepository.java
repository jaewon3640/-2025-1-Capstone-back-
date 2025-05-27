package com.example.demo.repository;

import com.example.demo.UserRole;
import com.example.demo.domain.FcmToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {
    Optional<FcmToken> findByUserId(Long userId);
    List<FcmToken> findByRole(UserRole role);
    boolean existsByUserIdAndToken(Long userId, String token);
}