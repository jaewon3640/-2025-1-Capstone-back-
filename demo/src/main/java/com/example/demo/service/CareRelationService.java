package com.example.demo.service;


import com.example.demo.DTO.CareRelationRequest;
import com.example.demo.DTO.CareRelationResponse;
import com.example.demo.domain.CareRelation;
import com.example.demo.domain.User;
import com.example.demo.repository.CareRelationRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;




@Service
@RequiredArgsConstructor
public class CareRelationService {

    private final CareRelationRepository careRelationRepository;
    private final UserRepository userRepository;

    public CareRelationResponse connectFromDto(CareRelationRequest request) {
        User caregiver = userRepository.findById(request.getCaregiverId())
                .orElseThrow(() -> new RuntimeException("보호자 없음"));
        User protectedUser = userRepository.findById(request.getProtectedUserId())
                .orElseThrow(() -> new RuntimeException("피보호자 없음"));

        CareRelation relation = CareRelation.builder()
                .caregiver(caregiver)
                .protectedUser(protectedUser)
                .build();

        return new CareRelationResponse(careRelationRepository.save(relation));
    }

    public CareRelationResponse createRelationFromDto(CareRelationRequest request) {
        return connectFromDto(request);
    }

    public List<CareRelationResponse> getByCaregiverId(Long caregiverId) {
        return careRelationRepository.findByCaregiverId(caregiverId).stream()
                .map(CareRelationResponse::new)
                .collect(Collectors.toList());
    }

    public List<CareRelationResponse> getByProtectedUserId(Long protectedUserId) {
        return careRelationRepository.findByProtectedUserId(protectedUserId).stream()
                .map(CareRelationResponse::new)
                .collect(Collectors.toList());
    }

}