package com.apportion.apportion.Expenses.Repository;

import com.apportion.apportion.Expenses.Model.ContratoDeDivida;
import com.apportion.apportion.Expenses.Model.DespesasEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IDespesaRepository  extends JpaRepository<DespesasEntity, Long> {
}
