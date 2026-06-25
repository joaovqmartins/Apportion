package com.apportion.apportion.Expenses.Service;

import com.apportion.apportion.Expenses.Model.Dto.ContratoDeDividaResponseDTO;
import com.apportion.apportion.Expenses.Model.ContratoDeDivida;
import com.apportion.apportion.Expenses.Repository.IContratoDeDividaRepository;
import com.apportion.apportion.Identity.Model.Entidades.UsuarioEntity;
import com.apportion.apportion.Identity.Repositories.UserRepository;
import com.apportion.apportion.Social.Model.Entidades.ViagemEntity;
import com.apportion.apportion.Social.Repositories.ViagemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.apportion.apportion.Expenses.Exception.ResourceNotFoundException;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class ContratoDeDividaService {

    @Autowired
    private IContratoDeDividaRepository contratoDeDividaRepository;

    @Autowired
    private ViagemRepository viagemRepository;

    @Autowired
    private UserRepository usuarioRepository;

    public void criarContratoDeDivida(ViagemEntity viagem, UsuarioEntity recebedor,
                                      UsuarioEntity devedor, BigDecimal valorDaDespesa) {

        Optional<ContratoDeDivida> dividaInversaOpt = contratoDeDividaRepository
                .findByViagemAndCredorAndDevedor(viagem, devedor, recebedor);

        if (dividaInversaOpt.isPresent()) {
            ContratoDeDivida dividaInversa = dividaInversaOpt.get();
            int comparacao = dividaInversa.getValor().compareTo(valorDaDespesa);

            if (comparacao == 0) {
                contratoDeDividaRepository.delete(dividaInversa);
            } else if (comparacao > 0) {
                BigDecimal novoValor = dividaInversa.getValor().subtract(valorDaDespesa);
                dividaInversa.setValor(novoValor);
                contratoDeDividaRepository.save(dividaInversa);
            } else {
                BigDecimal valorInvertido = valorDaDespesa.subtract(dividaInversa.getValor());
                dividaInversa.setCredor(devedor);
                dividaInversa.setDevedor(recebedor);
                dividaInversa.setValor(valorInvertido);
                contratoDeDividaRepository.save(dividaInversa);
            }
        } else {
            Optional<ContratoDeDivida> dividaExistenteOpt = contratoDeDividaRepository
                    .findByViagemAndCredorAndDevedor(viagem, recebedor, devedor);

            if (dividaExistenteOpt.isPresent()) {
                ContratoDeDivida dividaExistente = dividaExistenteOpt.get();
                dividaExistente.setValor(dividaExistente.getValor().add(valorDaDespesa));
                contratoDeDividaRepository.save(dividaExistente);
            } else {
                ContratoDeDivida novoContrato = new ContratoDeDivida();
                novoContrato.setViagem(viagem);
                novoContrato.setCredor(recebedor);
                novoContrato.setDevedor(devedor);
                novoContrato.setValor(valorDaDespesa);
                contratoDeDividaRepository.save(novoContrato);
            }
        }
    }

    /**
     * Dívidas da viagem ativa do usuário (como credor ou devedor).
     */
    @Transactional(readOnly = true)
    public Page<ContratoDeDividaResponseDTO> findByViagemAtivaUser(Long userId, Pageable pageable) {
        usuarioRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuário não encontrado com id: " + userId));

        return contratoDeDividaRepository
                .findByViagemAtivaAndUsuarioId(userId, pageable)
                .map(ContratoDeDividaResponseDTO::from);
    }

    /**
     * Dívidas de uma viagem específica.
     */
    @Transactional(readOnly = true)
    public Page<ContratoDeDividaResponseDTO> findByViagem(Long viagemId, Pageable pageable) {
        viagemRepository.findById(viagemId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Viagem não encontrada com id: " + viagemId));

        return contratoDeDividaRepository
                .findByViagemId(viagemId, pageable)
                .map(ContratoDeDividaResponseDTO::from);
    }

    /**
     * Dívidas de viagens concluídas do usuário (como credor ou devedor).
     */
    @Transactional(readOnly = true)
    public Page<ContratoDeDividaResponseDTO> findByViagemConcluidaUser(Long userId, Pageable pageable) {
        usuarioRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuário não encontrado com id: " + userId));

        return contratoDeDividaRepository
                .findByViagemConcluidaAndUsuarioId(userId, pageable)
                .map(ContratoDeDividaResponseDTO::from);
    }
    /**
     * Dívidas ATIVAS onde o usuário é o CREDOR (tem a receber).
     */
    @Transactional(readOnly = true)
    public Page<ContratoDeDividaResponseDTO> findDividasAtivasComoCredor(Long userId, Pageable pageable) {
        usuarioRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuário não encontrado com id: " + userId));

        return contratoDeDividaRepository
                .findDividasAtivasComoCredor(userId, pageable)
                .map(ContratoDeDividaResponseDTO::from);
    }

    /**
     * Dívidas CONCLUÍDAS onde o usuário era o CREDOR.
     */
    @Transactional(readOnly = true)
    public Page<ContratoDeDividaResponseDTO> findDividasConcluidasComoCredor(Long userId, Pageable pageable) {
        usuarioRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuário não encontrado com id: " + userId));

        return contratoDeDividaRepository
                .findDividasConcluidasComoCredor(userId, pageable)
                .map(ContratoDeDividaResponseDTO::from);
    }

    /**
     * Dívidas ATIVAS onde o usuário é o DEVEDOR (tem a pagar).
     */
    @Transactional(readOnly = true)
    public Page<ContratoDeDividaResponseDTO> findDividasAtivasComoDevedor(Long userId, Pageable pageable) {
        usuarioRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuário não encontrado com id: " + userId));

        return contratoDeDividaRepository
                .findDividasAtivasComoDevedor(userId, pageable)
                .map(ContratoDeDividaResponseDTO::from);
    }

    /**
     * Dívidas CONCLUÍDAS onde o usuário era o DEVEDOR.
     */
    @Transactional(readOnly = true)
    public Page<ContratoDeDividaResponseDTO> findDividasConcluidasComoDevedor(Long userId, Pageable pageable) {
        usuarioRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuário não encontrado com id: " + userId));

        return contratoDeDividaRepository
                .findDividasConcluidasComoDevedor(userId, pageable)
                .map(ContratoDeDividaResponseDTO::from);
    }
}