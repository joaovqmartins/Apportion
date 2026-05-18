package com.apportion.apportion.Social.Model.Entidades.Dto.Requests;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ViagemRequestDto {
    private String nome;
    private Long grupoId;
}
