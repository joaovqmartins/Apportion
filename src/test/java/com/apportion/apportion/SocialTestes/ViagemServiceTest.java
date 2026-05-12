package com.apportion.apportion.SocialTestes;

import com.apportion.apportion.Social.Model.Entidades.Dto.Mapper.IViagemMapper;
import com.apportion.apportion.Social.Model.Entidades.Dto.Requests.ViagemRequestDto;
import com.apportion.apportion.Social.Model.Entidades.Dto.Responses.ViagemResponseDto;
import com.apportion.apportion.Social.Model.Entidades.GrupoEntity;
import com.apportion.apportion.Social.Model.Entidades.ViagemEntity;
import com.apportion.apportion.Social.Repositories.GrupoRepository;
import com.apportion.apportion.Social.Repositories.ViagemRepository;
import com.apportion.apportion.Social.Service.ViagemService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class ViagemServiceTest {

    @Mock
    private ViagemRepository viagemRepository;

    @Mock
    private GrupoRepository grupoRepository;

    @Mock
    private IViagemMapper mapper;

    @InjectMocks
    private ViagemService service;

    @Test
    void deveSalvarViagemComSucessoQuandoGrupoExistir() {
        ViagemRequestDto request = new ViagemRequestDto("Praia", 1L);
        GrupoEntity grupo = new GrupoEntity(1L, "Amigos", new HashSet<>(), new HashSet<>());
        ViagemEntity entitySemGrupo = new ViagemEntity(null, "Praia", null);
        ViagemEntity entidadeSalva = new ViagemEntity(1L, "Praia", grupo);
        ViagemResponseDto responseEsperada = new ViagemResponseDto(1L, "Praia", 1L);

        // O serviço vai primeiro buscar o grupo
        Mockito.when(grupoRepository.findById(1L)).thenReturn(Optional.of(grupo));
        // Depois mapear
        Mockito.when(mapper.toEntity(request)).thenReturn(entitySemGrupo);
        // Salvar
        Mockito.when(viagemRepository.save(entitySemGrupo)).thenReturn(entidadeSalva);
        // E transformar em DTO
        Mockito.when(mapper.toResponseDTO(entidadeSalva)).thenReturn(responseEsperada);

        ViagemResponseDto resultado = service.save(request);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Praia", resultado.getNome());
        assertEquals(1L, resultado.getGrupoId());
    }

    @Test
    void deveLancarExcecaoAoTentarSalvarViagemComGrupoInexistente() {
        ViagemRequestDto request = new ViagemRequestDto("Praia", 99L); // Grupo 99 não existe

        Mockito.when(grupoRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            service.save(request);
        });

        assertEquals("Grupo não encontrado para criar a viagem", exception.getMessage());
        // Garante que o método save NUNCA foi chamado no repositório de viagens
        Mockito.verify(viagemRepository, Mockito.never()).save(any());
    }

    @Test
    void deveBuscarViagemPorIdComSucesso() {
        GrupoEntity grupo = new GrupoEntity(1L, "Amigos", new HashSet<>(), new HashSet<>());
        ViagemEntity entity = new ViagemEntity(1L, "Praia", grupo);
        ViagemResponseDto responseEsperada = new ViagemResponseDto(1L, "Praia", 1L);

        Mockito.when(viagemRepository.findById(1L)).thenReturn(Optional.of(entity));
        Mockito.when(mapper.toResponseDTO(entity)).thenReturn(responseEsperada);

        ViagemResponseDto resultado = service.findById(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
    }

    @Test
    void deveLancarExcecaoAoBuscarViagemPorIdInexistente() {
        Mockito.when(viagemRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            service.findById(99L);
        });

        assertEquals("Viagem não encontrada", exception.getMessage());
    }

    @Test
    void deveBuscarTodasAsViagensPaginadas() {
        Pageable pageable = PageRequest.of(0, 10);
        GrupoEntity grupo = new GrupoEntity(1L, "Amigos", new HashSet<>(), new HashSet<>());
        ViagemEntity entity = new ViagemEntity(1L, "Praia", grupo);
        Page<ViagemEntity> pagina = new PageImpl<>(List.of(entity));
        ViagemResponseDto responseDto = new ViagemResponseDto(1L, "Praia", 1L);

        Mockito.when(viagemRepository.findAll(pageable)).thenReturn(pagina);
        Mockito.when(mapper.toResponseDTO(any(ViagemEntity.class))).thenReturn(responseDto);

        Page<ViagemResponseDto> resultado = service.findAll(pageable);

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        assertEquals("Praia", resultado.getContent().get(0).getNome());
    }

    @Test
    void deveDeletarViagemComSucesso() {
        service.deleteById(1L);
        Mockito.verify(viagemRepository, Mockito.times(1)).deleteById(1L);
    }

    @Test
    void deveRetornarTrueSeViagemExiste() {
        Mockito.when(viagemRepository.existsById(1L)).thenReturn(true);
        boolean existe = service.existsById(1L);
        assertTrue(existe);
    }
}