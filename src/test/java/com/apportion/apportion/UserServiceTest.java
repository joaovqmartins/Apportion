package com.apportion.apportion;

import com.apportion.apportion.Identity.Model.Entidades.Dto.Mapper.IUsuarioMapper;
import com.apportion.apportion.Identity.Model.Entidades.Dto.Requests.UserRequestDto;
import com.apportion.apportion.Identity.Model.Entidades.Dto.Responses.UserResponseDto;
import com.apportion.apportion.Identity.Model.Entidades.UsuarioEntity;
import com.apportion.apportion.Identity.Repositrories.UserRepository;
import com.apportion.apportion.Identity.Service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.OffsetDateTime;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest
{
    @Mock
    private UserRepository repository;

    @Mock
    private IUsuarioMapper mapper;

    @InjectMocks
    private UserService service;

    @Test
    void deveSalvarUsuarioComSucesso()
    {
        LocalDate dataNascimentoFixa = LocalDate.of(1995, 10, 25);
        OffsetDateTime dataCriacaoFixa = OffsetDateTime.parse("2026-05-06T15:59:00-03:00");
        //Arrange
        UserRequestDto request = new UserRequestDto("Ana", "ana@email.com", "1234", dataNascimentoFixa);
        UsuarioEntity entity = new UsuarioEntity(null, "Ana", "ana@email.com", "1234", dataNascimentoFixa, null, null);
        UsuarioEntity entidadeSalva = new UsuarioEntity(1L, "Ana", "ana@email.com","1234", dataNascimentoFixa,dataCriacaoFixa, null );
        UserResponseDto responseEsperada = new UserResponseDto(1L, "Ana", "ana@email.com");

        Mockito.when(mapper.toEntity(request)).thenReturn(entity);
        Mockito.when(repository.save(entity)).thenReturn(entidadeSalva);
        Mockito.when(mapper.toResponseDTO(entidadeSalva)).thenReturn(responseEsperada);

        //Act
        UserResponseDto resultado = service.save(request);

        //Assert

        assertNotNull(resultado); // Garante que não é nulo
        assertEquals(1L, resultado.getId()); // Garante que o ID foi preenchido
        assertEquals("Ana", resultado.getNome()); // Garante que o nome está certo
        assertEquals("ana@email.com", resultado.getEmail()); // Garante que o email esta correto

        // Opcional: Verifica se o repository.save() foi realmente chamado 1 vez
        Mockito.verify(repository, Mockito.times(1)).save(entity);
    }
}
