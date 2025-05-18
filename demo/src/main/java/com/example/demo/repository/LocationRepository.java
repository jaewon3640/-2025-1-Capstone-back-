package com.example.demo.repository;


import com.example.demo.domain.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Long> {
    List<Location> findByProtectedUserId(Long protectedUserId);
    Optional<Location> findTopByProtectedUserIdOrderByTimestampDesc(Long protectedUserId);
}
