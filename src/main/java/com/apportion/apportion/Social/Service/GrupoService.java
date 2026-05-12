package com.apportion.apportion.Social.Service;

import com.apportion.apportion.Social.Model.Entidades.Dto.Mapper.IGrupoMapper;
import com.apportion.apportion.Social.Model.Entidades.Dto.Requests.GrupoRequestDto;
import com.apportion.apportion.Social.Model.Entidades.Dto.Responses.GrupoResponseDto;
import com.apportion.apportion.Social.Model.Entidades.GrupoEntity;
import com.apportion.apportion.Social.Repositories.GrupoRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
@Service
@AllArgsConstructor
public class GrupoService {

    private final GrupoRepository repository;
    private final IGrupoMapper mapper;

    public Page<GrupoResponseDto> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponseDTO);
    }

    public GrupoResponseDto findById(Long id) {
        return repository.findById(id)
                .map(mapper::toResponseDTO)
                .orElseThrow(() -> new RuntimeException("Grupo não encontrado"));
    }

    public GrupoResponseDto save(GrupoRequestDto request) {
        GrupoEntity entity = mapper.toEntity(request);
        GrupoEntity saved = repository.save(entity);
        return mapper.toResponseDTO(saved);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return repository.existsById(id);
    }
}

