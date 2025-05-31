package com.example.demo.service;


import com.example.demo.DTO.CareRelationRequest;
import com.example.demo.DTO.CareRelationResponse;
import com.example.demo.domain.CareRelation;
import com.example.demo.domain.User;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.repository.CareRelationRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CareRelationService {

    private final CareRelationRepository careRelationRepository;
    private final UserRepository userRepository;

    public CareRelationResponse connectFromDto(CareRelationRequest request) {
        User caregiver = userRepository.findById(request.getCaregiverId())
                .orElseThrow(() -> new UserNotFoundException("보호자 없음"));
        User protectedUser = userRepository.findById(request.getProtectedUserId())
                .orElseThrow(() -> new UserNotFoundException("피보호자 없음"));

        // 중복 관계 검사
        boolean exists = careRelationRepository.existsByCaregiverIdAndProtectedUserId(
                caregiver.getId(), protectedUser.getId());

        if (exists) {
            throw new IllegalStateException("이미 존재하는 보호자-피보호자 관계입니다.");
        }

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

    @Transactional
    public void deleteByCaregiverAndProtectedUser(Long caregiverId, Long protectedUserId) {
        careRelationRepository.deleteByCaregiverIdAndProtectedUserId(caregiverId, protectedUserId);
    }
}