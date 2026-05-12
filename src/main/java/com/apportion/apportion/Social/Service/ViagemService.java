package com.apportion.apportion.Social.Service;

import com.apportion.apportion.Social.Model.Entidades.Dto.Mapper.IViagemMapper;
import com.apportion.apportion.Social.Model.Entidades.Dto.Requests.ViagemRequestDto;
import com.apportion.apportion.Social.Model.Entidades.Dto.Responses.ViagemResponseDto;
import com.apportion.apportion.Social.Model.Entidades.GrupoEntity;
import com.apportion.apportion.Social.Model.Entidades.ViagemEntity;
import com.apportion.apportion.Social.Repositories.GrupoRepository;
import com.apportion.apportion.Social.Repositories.ViagemRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ViagemService {

    private final ViagemRepository viagemRepository;
    private final GrupoRepository grupoRepository;
    private final IViagemMapper mapper;

    public Page<ViagemResponseDto> findAll(Pageable pageable) {
        return viagemRepository.findAll(pageable).map(mapper::toResponseDTO);
    }

    public ViagemResponseDto findById(Long id) {
        return viagemRepository.findById(id)
                .map(mapper::toResponseDTO)
                .orElseThrow(() -> new RuntimeException("Viagem não encontrada"));
    }

    public ViagemResponseDto save(ViagemRequestDto request) {
        GrupoEntity grupo = grupoRepository.findById(request.getGrupoId())
                .orElseThrow(() -> new RuntimeException("Grupo não encontrado para criar a viagem"));

        ViagemEntity entity = mapper.toEntity(request);
        entity.setGrupo(grupo);

        ViagemEntity saved = viagemRepository.save(entity);
        return mapper.toResponseDTO(saved);
    }

    public void deleteById(Long id) {
        viagemRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return viagemRepository.existsById(id);
    }
}