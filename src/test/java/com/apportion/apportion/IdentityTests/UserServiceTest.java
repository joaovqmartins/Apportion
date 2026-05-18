package com.apportion.apportion.IdentityTests;

import com.apportion.apportion.Identity.Model.Entidades.Dto.Mapper.IUsuarioMapper;
import com.apportion.apportion.Identity.Model.Entidades.Dto.Requests.UserRequestDto;
import com.apportion.apportion.Identity.Model.Entidades.Dto.Responses.UserResponseDto;
import com.apportion.apportion.Identity.Model.Entidades.UsuarioEntity;
import com.apportion.apportion.Identity.Repositories.UserRepository;
import com.apportion.apportion.Identity.Service.UserService;
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

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository repository;

    @Mock
    private IUsuarioMapper mapper;

    @InjectMocks
    private UserService service;

    @Test
    void deveSalvarUsuarioComSucesso() {
        LocalDate dataNascimentoFixa = LocalDate.of(1995, 10, 25);
        OffsetDateTime dataCriacaoFixa = OffsetDateTime.parse("2026-05-06T15:59:00-03:00");

        // Arrange
        UserRequestDto request = new UserRequestDto("Ana", "ana@email.com", "1234", dataNascimentoFixa);
        UsuarioEntity entity = new UsuarioEntity(null, "Ana", "ana@email.com", "1234", dataNascimentoFixa, null, null);
        UsuarioEntity entidadeSalva = new UsuarioEntity(1L, "Ana", "ana@email.com","1234", dataNascimentoFixa,dataCriacaoFixa, null );
        UserResponseDto responseEsperada = new UserResponseDto(1L, "Ana", "ana@email.com");

        Mockito.when(mapper.toEntity(request)).thenReturn(entity);
        Mockito.when(repository.save(entity)).thenReturn(entidadeSalva);
        Mockito.when(mapper.toResponseDTO(entidadeSalva)).thenReturn(responseEsperada);

        // Act
        UserResponseDto resultado = service.save(request);

        // Assert
        assertNotNull(resultado); // Garante que não é nulo
        assertEquals(1L, resultado.getId()); // Garante que o ID foi preenchido
        assertEquals("Ana", resultado.getNome()); // Garante que o nome está certo
        assertEquals("ana@email.com", resultado.getEmail()); // Garante que o email esta correto

        // Opcional: Verifica se o repository.save() foi realmente chamado 1 vez
        Mockito.verify(repository, Mockito.times(1)).save(entity);
    }

    @Test
    void deveBuscarUsuarioPorIdComSucesso() {
        // Arrange
        UsuarioEntity entity = new UsuarioEntity(1L, "João", "joao@email.com", "1234", null, null, null);
        UserResponseDto responseEsperada = new UserResponseDto(1L, "João", "joao@email.com");

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(entity));
        Mockito.when(mapper.toResponseDTO(entity)).thenReturn(responseEsperada);

        // Act
        UserResponseDto resultado = service.findById(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("João", resultado.getNome());
    }

    @Test
    void deveLancarExcecaoAoBuscarUsuarioInexistente() {
        // Arrange
        Mockito.when(repository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            service.findById(99L);
        });

        assertEquals("Usuário não encontrado", exception.getMessage());
    }

    @Test
    void deveBuscarTodosOsUsuariosPaginados() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        UsuarioEntity entity = new UsuarioEntity(1L, "Carlos", "carlos@email.com", "123", null, null, null);
        Page<UsuarioEntity> pagina = new PageImpl<>(List.of(entity));
        UserResponseDto responseDto = new UserResponseDto(1L, "Carlos", "carlos@email.com");

        Mockito.when(repository.findAll(pageable)).thenReturn(pagina);
        Mockito.when(mapper.toResponseDTO(any(UsuarioEntity.class))).thenReturn(responseDto);

        // Act
        Page<UserResponseDto> resultado = service.findAll(pageable);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        assertEquals("Carlos", resultado.getContent().get(0).getNome());
    }

    @Test
    void deveDeletarUsuarioPorIdComSucesso() {
        // Act
        service.deletebyId(1L);

        // Assert
        Mockito.verify(repository, Mockito.times(1)).deleteById(1L);
    }

    @Test
    void deveRetornarTrueSeUsuarioExistir() {
        // Arrange
        Mockito.when(repository.existsById(1L)).thenReturn(true);

        // Act
        boolean existe = service.existsById(1L);

        // Assert
        assertTrue(existe);
    }
}