package com.apportion.apportion.Identity.Model.Entidades;

import com.apportion.apportion.Identity.Model.Enums.Roles;
import jakarta.persistence.*;
import lombok.*;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static com.apportion.apportion.Identity.Model.Enums.Roles.ADMIN;

@Entity
@Table(name = "Usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String senha;

    @Column(nullable = false)
    private LocalDate dataDeNascimento;

    @Column(nullable = false)
    private OffsetDateTime dataCriacao;

    @Column(nullable = false)
    private Roles role;

    @PrePersist
    public void OnCrate(){this.dataCriacao = OffsetDateTime.now();}

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if(this.role == ADMIN) return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"),new SimpleGrantedAuthority("ROLE_USER"));
        else return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public @Nullable String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return email;
    }
}