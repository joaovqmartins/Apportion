package com.apportion.apportion.Expenses.Repository;

import com.apportion.apportion.Expenses.Model.ContratoDeDivida;
import com.apportion.apportion.Identity.Model.Entidades.UsuarioEntity;
import com.apportion.apportion.Social.Model.Entidades.ViagemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IContratoDeDividaRepository extends JpaRepository<ContratoDeDivida, Long>{
    Optional<ContratoDeDivida> findByViagemAndCredorAndDevedor(
            ViagemEntity viagem,
            UsuarioEntity credor,
            UsuarioEntity devedor
    );
}
