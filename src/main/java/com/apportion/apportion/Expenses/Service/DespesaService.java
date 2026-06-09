package com.apportion.apportion.Expenses.Service;

import com.apportion.apportion.Expenses.Model.DespesasEntity;
import com.apportion.apportion.Expenses.Repository.IDespesaRepository;
import com.apportion.apportion.Identity.Model.Entidades.UsuarioEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class DespesaService {
    @Autowired
    private IDespesaRepository despesaRepository;
    @Autowired
    private ContratoDeDividaService contratoDeDividaService;

    @Transactional
    public void registrarNovaDespesa(DespesasEntity despesa){
        despesaRepository.save(despesa);

        //calculo de divisao da dispesa
        int totalPagantes = despesa.getPagantes().size();
        if(totalPagantes == 0){
            throw new IllegalArgumentException("A despesa precisa ter pelo menos um pagante");
        }
        BigDecimal divisor = new BigDecimal(totalPagantes + 1 );

        BigDecimal valorPorPessoa = despesa.getValor().divide(divisor,2, RoundingMode.HALF_UP);

        //loop para atualizar o saldo dos pagantes
        for(UsuarioEntity devedor : despesa.getPagantes()){
            //verifica se o id do recebedor nao esta entre os pagantes
            if(!devedor.getId().equals(despesa.getRecebedor().getId())){
                //Cria ou atualiza o contrato de divida conforme as regras de negocio do contratoDeDividaService
                contratoDeDividaService.criarContratoDeDivida(
                        despesa.getViagem(),
                        despesa.getRecebedor(),
                        devedor,
                        valorPorPessoa
                );
            }
        }



    }
}
