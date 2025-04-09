package com.example.demo.DTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CareRelationRequest {
    private Long caregiverId;
    private Long protectedUserId;
}