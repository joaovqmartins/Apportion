package com.apportion.apportion.Identity.Model.Entidades;

import com.apportion.apportion.Expenses.Model.DispesasEntity;
import com.apportion.apportion.Identity.Model.Enums.Roles;
import com.apportion.apportion.Social.Model.Entidades.GrupoEntity;
import jakarta.persistence.*;
import lombok.*;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.*;

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

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String senha;

    @Column(nullable = false)
    private LocalDate dataDeNascimento;

    @Column(nullable = false)
    private OffsetDateTime dataCriacao;

    @Column(nullable = false)
    private Roles role;

    @ManyToMany
    @JoinTable(name = "usuario_grupos",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "grupo_id"))
    private Set<GrupoEntity> grupo = new HashSet<>();

    @OneToMany(mappedBy = "recebedor", cascade = CascadeType.ALL)
    private Set<DispesasEntity> dispesasParaReceber = new HashSet<>();

    @ManyToMany(mappedBy = "pagantes")
    private Set<DispesasEntity> dispesasParaPagar = new HashSet<>();

    public UsuarioEntity(String nome, String email, String senha, LocalDate dataDeNascimento, Roles role){
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.dataDeNascimento = dataDeNascimento;
        this.role = role;

    }

    @PrePersist
    public void OnCrate(){this.dataCriacao = OffsetDateTime.now();}

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if(this.role == ADMIN) return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"),new SimpleGrantedAuthority("ROLE_USER"));
        else return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public @Nullable String getPassword() {
        return this.senha;
    }

    @Override
    public String getUsername() {
        return this.email;
    }
}