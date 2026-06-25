package com.apportion.apportion.IdentityTests;
import com.apportion.apportion.Identity.Model.Entidades.Dto.Mapper.IUsuarioMapper;
import com.apportion.apportion.Identity.Model.Entidades.Dto.Requests.UserRequestDto;
import com.apportion.apportion.Identity.Model.Entidades.Dto.Responses.UserResponseDto;
import com.apportion.apportion.Identity.Model.Entidades.UsuarioEntity;
import com.apportion.apportion.Identity.Model.Enums.Roles;
import com.apportion.apportion.Identity.Repositories.UserRepository;
import com.apportion.apportion.Identity.Service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioEntityTest {

    private UsuarioEntity usuario;

    @BeforeEach
    void setUp() {
        // Inicializa uma entidade básica antes de cada teste
        usuario = new UsuarioEntity(
                "João Silva",
                "joao.silva@email.com",
                "senha123",
                LocalDate.of(1990, 5, 15),
                Roles.USER // Assumindo que você tem um Roles.USER no seu enum
        );
    }

    @Test
    @DisplayName("Deve inicializar a entidade corretamente usando o construtor customizado")
    void customConstructor_ShouldInitializeFieldsCorrectly() {
        assertEquals("João Silva", usuario.getNome());
        assertEquals("joao.silva@email.com", usuario.getEmail());
        assertEquals("senha123", usuario.getSenha());
        assertEquals(LocalDate.of(1990, 5, 15), usuario.getDataDeNascimento());
        assertEquals(Roles.USER, usuario.getRole());
    }

    @Test
    @DisplayName("As coleções devem ser inicializadas vazias e não nulas ao criar uma nova entidade")
    void collections_ShouldBeInitializedEmpty() {
        UsuarioEntity novoUsuario = new UsuarioEntity();

        assertNotNull(novoUsuario.getGrupo());
        assertTrue(novoUsuario.getGrupo().isEmpty());

        assertNotNull(novoUsuario.getDespesasParaReceber());
        assertTrue(novoUsuario.getDespesasParaReceber().isEmpty());

        assertNotNull(novoUsuario.getDespesasParaPagar());
        assertTrue(novoUsuario.getDespesasParaPagar().isEmpty());

        assertNotNull(novoUsuario.getCreditosAtivos());
        assertTrue(novoUsuario.getCreditosAtivos().isEmpty());

        assertNotNull(novoUsuario.getDebitosAtivos());
        assertTrue(novoUsuario.getDebitosAtivos().isEmpty());
    }

    @Test
    @DisplayName("O método @PrePersist deve preencher a data de criação")
    void onCrate_ShouldSetDataCriacao() {
        assertNull(usuario.getDataCriacao()); // Garante que começa nulo

        usuario.onCrate(); // Executa o método de callback do JPA

        assertNotNull(usuario.getDataCriacao()); // Garante que foi preenchido com OffsetDateTime.now()
    }

    @Test
    @DisplayName("Deve retornar ROLE_ADMIN e ROLE_USER quando o usuário for ADMIN")
    void getAuthorities_ShouldReturnAdminAndUserRoles_WhenRoleIsAdmin() {
        usuario.setRole(Roles.ADMIN);

        Collection<? extends GrantedAuthority> authorities = usuario.getAuthorities();
        List<String> authorityNames = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        assertEquals(2, authorities.size());
        assertTrue(authorityNames.contains("ROLE_ADMIN"));
        assertTrue(authorityNames.contains("ROLE_USER"));
    }

    @Test
    @DisplayName("Deve retornar apenas ROLE_USER quando o usuário não for ADMIN")
    void getAuthorities_ShouldReturnOnlyUserRole_WhenRoleIsNotAdmin() {
        // O setup já definiu a role como algo diferente de ADMIN (ex: Roles.USER)
        Collection<? extends GrantedAuthority> authorities = usuario.getAuthorities();
        List<String> authorityNames = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        assertEquals(1, authorities.size());
        assertTrue(authorityNames.contains("ROLE_USER"));
        assertFalse(authorityNames.contains("ROLE_ADMIN"));
    }

    @Test
    @DisplayName("O método getPassword deve retornar a senha do usuário")
    void getPassword_ShouldReturnSenha() {
        assertEquals("senha123", usuario.getPassword());
    }

    @Test
    @DisplayName("O método getUsername deve retornar o e-mail do usuário")
    void getUsername_ShouldReturnEmail() {
        assertEquals("joao.silva@email.com", usuario.getUsername());
    }
}
}