package com.apportion.apportion.Expenses.Service;

import com.apportion.apportion.Expenses.Exception.ResourceNotFoundException;
import com.apportion.apportion.Expenses.Model.Dto.DespesaResponseDTO;
import com.apportion.apportion.Expenses.Model.DespesasEntity;
import com.apportion.apportion.Expenses.Repository.IDespesaRepository;
import com.apportion.apportion.Identity.Model.Entidades.UsuarioEntity;
import com.apportion.apportion.Identity.Repositories.UserRepository;
import com.apportion.apportion.Social.Model.Entidades.ViagemEntity;
import com.apportion.apportion.Social.Repositories.ViagemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class DespesaService {

    @Autowired
    private IDespesaRepository despesaRepository;

    @Autowired
    private ViagemRepository viagemRepository;

    @Autowired
    private UserRepository usuarioRepository;

    @Autowired
    private ContratoDeDividaService contratoDeDividaService;


    @Transactional
    public void registrarNovaDespesa(DespesasEntity despesa) {
        despesaRepository.save(despesa);

        int totalPagantes = despesa.getPagantes().size();
        if (totalPagantes == 0) {
            throw new IllegalArgumentException("A despesa precisa ter pelo menos um pagante");
        }
        BigDecimal divisor = new BigDecimal(totalPagantes + 1);
        BigDecimal valorPorPessoa = despesa.getValor().divide(divisor, 2, RoundingMode.HALF_UP);

        for (UsuarioEntity devedor : despesa.getPagantes()) {
            if (!devedor.getId().equals(despesa.getRecebedor().getId())) {
                contratoDeDividaService.criarContratoDeDivida(
                        despesa.getViagem(),
                        despesa.getRecebedor(),
                        devedor,
                        valorPorPessoa
                );
            }
        }
    }

    // ------------------------------------------------------------------
    // Novos métodos de consulta
    // ------------------------------------------------------------------

    /**
     * Busca todas as despesas de uma viagem, paginadas.
     *
     * @throws ResourceNotFoundException se a viagem não existir
     */
    @Transactional(readOnly = true)
    public Page<DespesaResponseDTO> findByViagem(Long viagemId, Pageable pageable) {
        ViagemEntity viagem = viagemRepository.findById(viagemId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Viagem não encontrada com id: " + viagemId));

        return despesaRepository
                .findByViagemId(viagem.getId(), pageable)
                .map(DespesaResponseDTO::from);
    }

    /**
     * Busca todas as despesas em que o usuário participa (recebedor ou pagante), paginadas.
     *
     * @throws ResourceNotFoundException se o usuário não existir
     */
    @Transactional(readOnly = true)
    public Page<DespesaResponseDTO> findByUser(Long userId, Pageable pageable) {
        usuarioRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuário não encontrado com id: " + userId));

        return despesaRepository
                .findByUsuarioIdAsRecebedorOrPagante(userId, pageable)
                .map(DespesaResponseDTO::from);
    }
}