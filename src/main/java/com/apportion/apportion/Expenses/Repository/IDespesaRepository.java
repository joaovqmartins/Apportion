package com.apportion.apportion.Expenses.Repository;

import com.apportion.apportion.Expenses.Model.ContratoDeDivida;
import com.apportion.apportion.Expenses.Model.DespesasEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IDespesaRepository  extends JpaRepository<DespesasEntity, Long> {
    /**
     * Retorna todas as despesas de uma viagem, paginadas.
     */
    Page<DespesasEntity> findByViagemId(Long viagemId, Pageable pageable);

    /**
     * Retorna todas as despesas em que o usuário é o recebedor
     * OU aparece na lista de pagantes.
     */
    @Query("""
        SELECT DISTINCT d FROM DespesasEntity d
        LEFT JOIN d.pagantes p
        WHERE d.recebedor.id = :userId
           OR p.id = :userId
    """)
    Page<DespesasEntity> findByUsuarioIdAsRecebedorOrPagante(
            @Param("userId") Long userId,
            Pageable pageable);
}
