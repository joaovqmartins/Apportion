package com.apportion.apportion.Identity.Service;

import com.apportion.apportion.Identity.Model.Entidades.Dto.Mapper.IUsuarioMapper;
import com.apportion.apportion.Identity.Model.Entidades.Dto.Requests.UserRequestDto;
import com.apportion.apportion.Identity.Model.Entidades.Dto.Responses.UserResponseDto;
import com.apportion.apportion.Identity.Model.Entidades.UsuarioEntity;
import com.apportion.apportion.Identity.Repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository _repository;
    private final IUsuarioMapper _mapper;

    // O construtor manual que estava aqui foi removido!

    public Page<UserResponseDto> findAll(Pageable pageable) {
        Page<UsuarioEntity> usuarios = _repository.findAll(pageable);
        return usuarios.map(usuarioEntity -> _mapper.toResponseDTO(usuarioEntity));
    }

    public UserResponseDto findById(Long id) {
        return _repository.findById(id)
                .map(entity -> _mapper.toResponseDTO(entity))
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    public UserResponseDto save(UserRequestDto request) {
        UsuarioEntity userToSave = _mapper.toEntity(request);
        UsuarioEntity userSaved = _repository.save(userToSave);
        return _mapper.toResponseDTO(userSaved);
    }

    public void deletebyId(Long id) {
        _repository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return _repository.existsById(id);
    }
}