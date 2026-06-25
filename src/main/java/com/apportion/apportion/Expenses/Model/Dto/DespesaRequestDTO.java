package com.apportion.apportion.Expenses.Model.Dto;

import com.apportion.apportion.Expenses.Model.DespesasEntity;
import com.apportion.apportion.Identity.Model.Entidades.UsuarioEntity;
import com.apportion.apportion.Social.Model.Entidades.ViagemEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public record DespesaRequestDTO(
        String descricao,
        BigDecimal valor,
        Long viagemId,
        Long recebedorId,
        List<Long> pagantesIds
) {
    public DespesasEntity toEntity() {
        DespesasEntity despesa = new DespesasEntity();
        despesa.setDescricao(this.descricao);
        despesa.setValor(this.valor);

        // Vincula a Viagem (instanciando apenas com o ID para o JPA resolver)
        ViagemEntity viagem = new ViagemEntity();
        viagem.setId(this.viagemId);
        despesa.setViagem(viagem);

        // Vincula o Recebedor
        UsuarioEntity recebedor = new UsuarioEntity();
        recebedor.setId(this.recebedorId);
        despesa.setRecebedor(recebedor);

        // Vincula os Pagantes
        Set<UsuarioEntity> pagantes = this.pagantesIds.stream().map(id -> {
            UsuarioEntity usuario = new UsuarioEntity();
            usuario.setId(id);
            return usuario;
        }).collect(Collectors.toSet());
        despesa.setPagantes(pagantes);

        return despesa;
    }
}