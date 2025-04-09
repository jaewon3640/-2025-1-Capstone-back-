package com.example.demo.repository;


import com.example.demo.domain.CareRelation;
import com.example.demo.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface CareRelationRepository extends JpaRepository<CareRelation, Long> {
    List<CareRelation> findByCaregiver(User caregiver);
    List<CareRelation> findByProtectedUser(User protectedUser);
}