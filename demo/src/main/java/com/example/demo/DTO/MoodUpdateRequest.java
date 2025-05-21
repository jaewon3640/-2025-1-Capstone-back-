package com.example.demo.DTO;

import com.example.demo.MoodStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MoodUpdateRequest {
    private MoodStatus mood;
}
