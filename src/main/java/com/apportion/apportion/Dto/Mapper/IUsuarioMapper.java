package com.apportion.apportion.Dto.Mapper;

import com.apportion.apportion.Dto.Requests.UserRequestDto;
import com.apportion.apportion.Dto.Responses.UserResponseDto;
import com.apportion.apportion.Model.Entidades.UsuarioEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IUsuarioMapper
{
    UsuarioEntity toEntity(UserRequestDto requestDto);

    UserResponseDto toResponseDTO(UsuarioEntity entity);
}
