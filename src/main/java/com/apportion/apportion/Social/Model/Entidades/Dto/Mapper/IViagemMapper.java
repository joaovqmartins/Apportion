package com.apportion.apportion.Social.Model.Entidades.Dto.Mapper;

import com.apportion.apportion.Social.Model.Entidades.Dto.Requests.GrupoRequestDto;
import com.apportion.apportion.Social.Model.Entidades.Dto.Responses.GrupoResponseDto;
import com.apportion.apportion.Social.Model.Entidades.Dto.Responses.ViagemResponseDto;
import com.apportion.apportion.Social.Model.Entidades.GrupoEntity;
import com.apportion.apportion.Social.Model.Entidades.ViagemEntity;
import org.mapstruct.Mapper;

import com.apportion.apportion.Social.Model.Entidades.Dto.Requests.ViagemRequestDto;
import com.apportion.apportion.Social.Model.Entidades.Dto.Responses.ViagemResponseDto;
import com.apportion.apportion.Social.Model.Entidades.ViagemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IViagemMapper {

    @Mapping(source = "grupoId", target = "grupo.id")
    ViagemEntity toEntity(ViagemRequestDto requestDto);

    @Mapping(source = "grupo.id", target = "grupoId")
    ViagemResponseDto toResponseDTO(ViagemEntity entity);
}

