package com.apportion.apportion.Social.Model.Entidades;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

}
