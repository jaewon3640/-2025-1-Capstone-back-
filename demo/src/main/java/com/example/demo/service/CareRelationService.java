package com.example.demo.service;


import com.example.demo.domain.CareRelation;
import com.example.demo.domain.User;
import com.example.demo.repository.CareRelationRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class CareRelationService {

    private final CareRelationRepository careRelationRepository;
    private final UserRepository userRepository;

    public CareRelation connect(Long caregiverId, Long protectedUserId) {
        User caregiver = userRepository.findById(caregiverId)
                .orElseThrow(() -> new RuntimeException("보호자 없음"));
        User protectedUser = userRepository.findById(protectedUserId)
                .orElseThrow(() -> new RuntimeException("피보호자 없음"));

        CareRelation relation = CareRelation.builder()
                .caregiver(caregiver)
                .protectedUser(protectedUser)
                .build();

        return careRelationRepository.save(relation);
    }

    public CareRelation createRelation(CareRelation relation) {
        return careRelationRepository.save(relation);
    }

    public List<CareRelation> getByCaregiver(User caregiver) {
        return careRelationRepository.findByCaregiver(caregiver);
    }

    public List<CareRelation> getByProtectedUser(User protectedUser) {
        return careRelationRepository.findByProtectedUser(protectedUser);
    }
}