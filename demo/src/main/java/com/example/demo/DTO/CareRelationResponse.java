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

    public CareRelationResponse (CareRelation relation) {
       this.relationId=relation.getId();
       this.caregiverId=relation.getCaregiver().getId();
       this.caregiverName=relation.getCaregiver().getName();
       this.protectedUserId=relation.getProtectedUser().getId();
       this.protectedUserName=relation.getProtectedUser().getName();
    }
}