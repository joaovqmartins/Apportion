package com.apportion.apportion.Social.Model.Entidades.Dto.Mapper;

import com.apportion.apportion.Social.Model.Entidades.Dto.Requests.GrupoRequestDto;
import com.apportion.apportion.Social.Model.Entidades.Dto.Responses.GrupoResponseDto;
import com.apportion.apportion.Social.Model.Entidades.GrupoEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IGrupoMapper {
    GrupoEntity toEntity(GrupoRequestDto requestDto);
    GrupoResponseDto toResponseDTO(GrupoEntity entity);
}
