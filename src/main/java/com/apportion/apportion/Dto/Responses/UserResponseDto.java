package com.apportion.apportion.Dto.Responses;

import jakarta.persistence.Entity;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponseDto {
    public long id;
    private String nome;
    private String email;
}
