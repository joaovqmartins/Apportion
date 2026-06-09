package com.apportion.apportion.Social.Model.Entidades.Dto.Responses;

import com.apportion.apportion.Identity.Model.Entidades.Dto.Responses.UserResponseDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GrupoResponseDto {
    private Long id;
    private String nomeDoGrupo;

    @JsonIgnoreProperties("grupos")
    private Set<UserResponseDto> usuarios;
}
