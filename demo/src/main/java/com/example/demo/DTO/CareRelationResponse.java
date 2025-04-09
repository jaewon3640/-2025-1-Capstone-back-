package com.example.demo.DTO;

import com.example.demo.domain.CareRelation;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CareRelationResponse {
    private Long relationId;
    private Long caregiverId;
    private String caregiverName;
    private Long protectedUserId;
    private String protectedUserName;

    public static CareRelationResponse from(CareRelation relation) {
        return CareRelationResponse.builder()
                .relationId(relation.getId())
                .caregiverId(relation.getCaregiver().getId())
                .caregiverName(relation.getCaregiver().getName())
                .protectedUserId(relation.getProtectedUser().getId())
                .protectedUserName(relation.getProtectedUser().getName())
                .build();
    }
}