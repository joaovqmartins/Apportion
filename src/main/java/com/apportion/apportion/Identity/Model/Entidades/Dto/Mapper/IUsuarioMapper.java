package com.apportion.apportion.Identity.Model.Entidades.Dto.Mapper;

import com.apportion.apportion.Identity.Model.Entidades.Dto.Requests.UserRequestDto;
import com.apportion.apportion.Identity.Model.Entidades.Dto.Responses.UserResponseDto;
import com.apportion.apportion.Identity.Model.Entidades.UsuarioEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IUsuarioMapper
{

    UsuarioEntity toEntity(UserRequestDto requestDto);

    UserResponseDto toResponseDTO(UsuarioEntity entity);
}
