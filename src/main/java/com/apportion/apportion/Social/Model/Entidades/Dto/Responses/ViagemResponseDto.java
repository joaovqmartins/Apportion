package com.apportion.apportion.Social.Model.Entidades.Dto.Responses;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ViagemResponseDto {
    private Long id;
    private String nome;
    private Long grupoId;
}
