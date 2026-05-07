package com.apportion.apportion.Social.Model.Entidades;

import com.apportion.apportion.Identity.Model.Entidades.UsuarioEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "grupos")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GrupoEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "NomeDoGrupo", nullable = false)
    private String nomeDoGrupo;
    @ManyToMany(mappedBy = "grupo")
    private Set<UsuarioEntity> usuarios = new HashSet<>();
    @OneToMany(mappedBy = "grupo",cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ViagemEntity> viagens = new HashSet<>();

}
