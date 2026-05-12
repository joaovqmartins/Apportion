package com.apportion.apportion.SocialTestes;

import com.apportion.apportion.Social.Model.Entidades.Dto.Mapper.IGrupoMapper;
import com.apportion.apportion.Social.Model.Entidades.Dto.Requests.GrupoRequestDto;
import com.apportion.apportion.Social.Model.Entidades.Dto.Responses.GrupoResponseDto;
import com.apportion.apportion.Social.Model.Entidades.GrupoEntity;
import com.apportion.apportion.Social.Repositories.GrupoRepository;
import com.apportion.apportion.Social.Service.GrupoService;
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
public class GrupoServiceTest {

    @Mock
    private GrupoRepository repository;

    @Mock
    private IGrupoMapper mapper;

    @InjectMocks
    private GrupoService service;

    @Test
    void deveSalvarGrupoComSucesso() {
        GrupoRequestDto request = new GrupoRequestDto("Viagem de Fim de Ano");
        GrupoEntity entity = new GrupoEntity(null, "Viagem de Fim de Ano", new HashSet<>(), new HashSet<>());
        GrupoEntity entidadeSalva = new GrupoEntity(1L, "Viagem de Fim de Ano", new HashSet<>(), new HashSet<>());
        GrupoResponseDto responseEsperada = new GrupoResponseDto(1L, "Viagem de Fim de Ano");

        Mockito.when(mapper.toEntity(request)).thenReturn(entity);
        Mockito.when(repository.save(entity)).thenReturn(entidadeSalva);
        Mockito.when(mapper.toResponseDTO(entidadeSalva)).thenReturn(responseEsperada);

        GrupoResponseDto resultado = service.save(request);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Viagem de Fim de Ano", resultado.getNomeDoGrupo());
        Mockito.verify(repository, Mockito.times(1)).save(entity);
    }

    @Test
    void deveBuscarGrupoPorIdComSucesso() {
        GrupoEntity entity = new GrupoEntity(1L, "Amigos", new HashSet<>(), new HashSet<>());
        GrupoResponseDto responseEsperada = new GrupoResponseDto(1L, "Amigos");

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(entity));
        Mockito.when(mapper.toResponseDTO(entity)).thenReturn(responseEsperada);

        GrupoResponseDto resultado = service.findById(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
    }

    @Test
    void deveLancarExcecaoAoBuscarGrupoPorIdInexistente() {
        Mockito.when(repository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            service.findById(99L);
        });

        assertEquals("Grupo não encontrado", exception.getMessage());
    }

    @Test
    void deveBuscarTodosOsGruposPaginados() {
        Pageable pageable = PageRequest.of(0, 10);
        GrupoEntity entity = new GrupoEntity(1L, "Amigos", new HashSet<>(), new HashSet<>());
        Page<GrupoEntity> pagina = new PageImpl<>(List.of(entity));
        GrupoResponseDto responseDto = new GrupoResponseDto(1L, "Amigos");

        Mockito.when(repository.findAll(pageable)).thenReturn(pagina);
        Mockito.when(mapper.toResponseDTO(any(GrupoEntity.class))).thenReturn(responseDto);

        Page<GrupoResponseDto> resultado = service.findAll(pageable);

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        assertEquals("Amigos", resultado.getContent().get(0).getNomeDoGrupo());
    }

    @Test
    void deveDeletarGrupoComSucesso() {
        service.deleteById(1L);
        Mockito.verify(repository, Mockito.times(1)).deleteById(1L);
    }

    @Test
    void deveRetornarTrueSeGrupoExiste() {
        Mockito.when(repository.existsById(1L)).thenReturn(true);
        boolean existe = service.existsById(1L);
        assertTrue(existe);
    }
}