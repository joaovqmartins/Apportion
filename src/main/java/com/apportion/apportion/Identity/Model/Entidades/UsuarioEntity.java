package com.apportion.apportion.Identity.Model.Entidades;

import com.apportion.apportion.Social.Model.Entidades.GrupoEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioEntity {

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

    @ManyToMany
    @JoinTable(name = "usuario_grupos",
    joinColumns = @JoinColumn(name = "usuario_id"),
    inverseJoinColumns = @JoinColumn(name = "grupo_id"))
    private Set<GrupoEntity> grupo = new HashSet<>();

    @PrePersist
    public void OnCrate(){this.dataCriacao = OffsetDateTime.now();}
}