package com.apportion.apportion.Social.Model.Entidades;


import com.apportion.apportion.Expenses.Model.DispesasEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "viagem")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ViagemEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    @ManyToOne
    @JoinColumn(name = "grupo_id", nullable = false)
    private GrupoEntity grupo;

    @OneToMany(mappedBy = "viagem", cascade = CascadeType.ALL)
    private Set<DispesasEntity> dispesas = new HashSet<>();

}
