package com.apportion.apportion.Expenses.Repository;

import com.apportion.apportion.Expenses.Model.ContratoDeDivida;
import com.apportion.apportion.Identity.Model.Entidades.UsuarioEntity;
import com.apportion.apportion.Social.Model.Entidades.ViagemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface IContratoDeDividaRepository extends JpaRepository<ContratoDeDivida, Long> {


    Optional<ContratoDeDivida> findByViagemAndCredorAndDevedor(
            ViagemEntity viagem,
            UsuarioEntity credor,
            UsuarioEntity devedor);

    /**
     * Retorna todas as dívidas de uma viagem (independente de status), paginadas.
     */
    Page<ContratoDeDivida> findByViagemId(Long viagemId, Pageable pageable);

    /**
     * Retorna dívidas da viagem ATIVA do usuário.
     * Considera o usuário tanto como credor quanto como devedor.
     * "Ativa" = viagem com status ATIVA (ajuste o valor do enum conforme o seu modelo).
     */
    @Query("""
        SELECT c FROM ContratoDeDivida c
        WHERE c.viagem.id = (
            SELECT v.id FROM ViagemEntity v
            JOIN v.membros m
            WHERE m.id = :userId
              AND v.status = 'ATIVA'
            ORDER BY v.dataInicio DESC
        )
        AND (c.credor.id = :userId OR c.devedor.id = :userId)
    """)
    Page<ContratoDeDivida> findByViagemAtivaAndUsuarioId(
            @Param("userId") Long userId,
            Pageable pageable);

    /**
     * Retorna todas as dívidas de viagens CONCLUÍDAS em que o usuário participa.
     */
    @Query("""
        SELECT c FROM ContratoDeDivida c
        JOIN c.viagem v
        JOIN v.membros m
        WHERE m.id = :userId
          AND v.status = 'CONCLUIDA'
          AND (c.credor.id = :userId OR c.devedor.id = :userId)
    """)
    Page<ContratoDeDivida> findByViagemConcluidaAndUsuarioId(
            @Param("userId") Long userId,
            Pageable pageable);

    /**
     * Retorna todas as dívidas ATIVAS (ativa = true) em que o usuário participa
     * (como credor ou como devedor), independente do status da viagem.
     */
    @Query("""
        SELECT c FROM ContratoDeDivida c
        WHERE c.ativa = true
          AND (c.credor.id = :userId OR c.devedor.id = :userId)
    """)
    Page<ContratoDeDivida> findDividasAtivasPorUsuarioId(
            @Param("userId") Long userId,
            Pageable pageable);

    /**
     * Retorna todas as dívidas CONCLUÍDAS/INATIVAS (ativa = false) em que o usuário participa
     * (como credor ou como devedor), independente do status da viagem.
     */
    @Query("""
        SELECT c FROM ContratoDeDivida c
        WHERE c.ativa = false
          AND (c.credor.id = :userId OR c.devedor.id = :userId)
    """)
    Page<ContratoDeDivida> findDividasConcluidasPorUsuarioId(
            @Param("userId") Long userId,
            Pageable pageable);
    /**
     * Retorna as dívidas ATIVAS onde o usuário é o CREDOR (tem a receber).
     */
    @Query("""
        SELECT c FROM ContratoDeDivida c
        WHERE c.ativa = true
          AND c.credor.id = :userId
    """)
    Page<ContratoDeDivida> findDividasAtivasComoCredor(
            @Param("userId") Long userId,
            Pageable pageable);

    /**
     * Retorna as dívidas CONCLUÍDAS onde o usuário era o CREDOR.
     */
    @Query("""
        SELECT c FROM ContratoDeDivida c
        WHERE c.ativa = false
          AND c.credor.id = :userId
    """)
    Page<ContratoDeDivida> findDividasConcluidasComoCredor(
            @Param("userId") Long userId,
            Pageable pageable);
    /**
     * Retorna as dívidas ATIVAS onde o usuário é o DEVEDOR (tem a pagar).
     */
    @Query("""
        SELECT c FROM ContratoDeDivida c
        WHERE c.ativa = true
          AND c.devedor.id = :userId
    """)
    Page<ContratoDeDivida> findDividasAtivasComoDevedor(
            @Param("userId") Long userId,
            Pageable pageable);

    /**
     * Retorna as dívidas CONCLUÍDAS onde o usuário era o DEVEDOR.
     */
    @Query("""
        SELECT c FROM ContratoDeDivida c
        WHERE c.ativa = false
          AND c.devedor.id = :userId
    """)
    Page<ContratoDeDivida> findDividasConcluidasComoDevedor(
            @Param("userId") Long userId,
            Pageable pageable);
}