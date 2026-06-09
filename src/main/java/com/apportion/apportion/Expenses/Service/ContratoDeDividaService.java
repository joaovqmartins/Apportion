package com.apportion.apportion.Expenses.Service;

import com.apportion.apportion.Expenses.Model.ContratoDeDivida;
import com.apportion.apportion.Expenses.Repository.IContratoDeDividaRepository;
import com.apportion.apportion.Identity.Model.Entidades.UsuarioEntity;
import com.apportion.apportion.Social.Model.Entidades.ViagemEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class ContratoDeDividaService {

    @Autowired
    private IContratoDeDividaRepository contratoDeDividaRepository;

    public void criarContratoDeDivida (ViagemEntity viagem, UsuarioEntity recebedor, UsuarioEntity devedor, BigDecimal valorDaDespesa){
        //verifica se o devedor da divida ja nao tem uma relacao de credor com o outro usuario
        Optional<ContratoDeDivida> dividaInversaOpt = contratoDeDividaRepository
                .findByViagemAndCredorAndDevedor(viagem, devedor, recebedor);

        if(dividaInversaOpt.isPresent()){
            ContratoDeDivida dividaInversa = dividaInversaOpt.get();
            
            int comparacao = dividaInversa.getValor().compareTo(valorDaDespesa);

            //Se a divida zerar deletamos a divida
            if(comparacao == 0){
                contratoDeDividaRepository.delete(dividaInversa);
            }// se houver divida e ela nao for zerada, subtraimos o valor da divida total
            else if (comparacao > 0) {
                BigDecimal novoValor = dividaInversa.getValor().subtract(valorDaDespesa);
                dividaInversa.setValor(novoValor);
                contratoDeDividaRepository.save(dividaInversa);
            }// Se o valor da dispesa passar o da divida, invertemos o contrato (agora quem deve é o antigo credor)
            else{
                BigDecimal valorInvertido = valorDaDespesa.subtract(dividaInversa.getValor());
                dividaInversa.setCredor(devedor);
                dividaInversa.setDevedor(recebedor);
                dividaInversa.setValor(valorInvertido);
                contratoDeDividaRepository.save(dividaInversa);
            }
        }else{
            Optional<ContratoDeDivida> dividaExistenteOpt = contratoDeDividaRepository
                    .findByViagemAndCredorAndDevedor(viagem, recebedor, devedor);
            if(dividaExistenteOpt.isPresent()){
                ContratoDeDivida dividaExistente = dividaExistenteOpt.get();
                //se a divida ja existia apenas somamos o valor na divida existente
                dividaExistente.setValor(dividaExistente.getValor().add(valorDaDespesa));
                contratoDeDividaRepository.save(dividaExistente);
            }
            else{
                //Se nao existe nenhuma divida previa entre nenhum dos dois, criamos uma.
                ContratoDeDivida novoContrato = new ContratoDeDivida();
                novoContrato.setViagem(viagem);
                novoContrato.setCredor(recebedor);
                novoContrato.setDevedor(devedor);
                novoContrato.setValor(valorDaDespesa);
                contratoDeDividaRepository.save(novoContrato);
            }
        }



    }
}
