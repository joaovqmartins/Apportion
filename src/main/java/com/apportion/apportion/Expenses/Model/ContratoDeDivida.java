package com.apportion.apportion.Expenses.Model;

import com.apportion.apportion.Identity.Model.Entidades.UsuarioEntity;
import com.apportion.apportion.Social.Model.Entidades.ViagemEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "contratos_de_divida", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"viagem_id", "credor_id", "devedor_id"})
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ContratoDeDivida {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "viagem_id", nullable = false)
    private ViagemEntity viagem;

    @ManyToOne
    @JoinColumn(name = "credor_id", nullable = false)
    private UsuarioEntity credor;

    @ManyToOne
    @JoinColumn(name = "devedor_id", nullable = false)
    private UsuarioEntity devedor;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Column(nullable = false)
    private boolean ativa;

}
