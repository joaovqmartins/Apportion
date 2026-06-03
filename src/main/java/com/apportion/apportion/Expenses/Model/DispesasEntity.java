package com.apportion.apportion.Expenses.Model;

import com.apportion.apportion.Identity.Model.Entidades.UsuarioEntity;
import com.apportion.apportion.Social.Model.Entidades.ViagemEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "dispesas")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DispesasEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "valor", nullable = false)
    private Double valor;

    @Column(name = "descricao", length = 213)
    private String descricao;

    @ManyToOne
    @JoinColumn(name = "recebedor_id")
    private UsuarioEntity recebedor;

    @ManyToMany
    @JoinTable(name = "pagantes_dispesas",
            joinColumns = @JoinColumn(name = "dispesa_id"),
            inverseJoinColumns = @JoinColumn(name = "pagante_id"))
    private Set<UsuarioEntity> pagantes = new HashSet<>();

    @ManyToOne()
    @JoinColumn(name = "viagem_id")
    private ViagemEntity viagem;











}
