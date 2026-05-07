package com.apportion.apportion.Service;

import com.apportion.apportion.Dto.Mapper.IUsuarioMapper;
import com.apportion.apportion.Dto.Requests.UserRequestDto;
import com.apportion.apportion.Dto.Responses.UserResponseDto;
import com.apportion.apportion.Model.Entidades.UsuarioEntity;
import com.apportion.apportion.Model.Repositrories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository _repository;

    @Autowired
    private IUsuarioMapper _mapper;

    public List<UserResponseDto> findAll() {
        List<UsuarioEntity> usuarios = _repository.findAll();
        List<UserResponseDto> responseUser = new ArrayList<>();

        for(UsuarioEntity entity : usuarios)
        {
            responseUser.add(_mapper.toResponseDTO(entity));
        }
        return responseUser;

    }

    public UserResponseDto findById(Long id) {
        return _repository.findById(id) // Retorna Optional<UsuarioEntity>
                .map(entity -> _mapper.toResponseDTO(entity)) // Transforma em UserResponseDto
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));          // Se não, retorna null
    }

    public UserResponseDto save(UserRequestDto request)
    {
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
