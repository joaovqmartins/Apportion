package com.apportion.apportion.Expenses.Model.Dto;


import com.apportion.apportion.Expenses.Model.ContratoDeDivida;
import com.apportion.apportion.Identity.Model.Entidades.Dto.Requests.UsuarioResumoDTO;

import java.math.BigDecimal;

public class ContratoDeDividaResponseDTO {

    private Long id;
    private BigDecimal valor;
    private UsuarioResumoDTO credor;
    private UsuarioResumoDTO devedor;
    private Long viagemId;
    private String nomeViagem;

    public static ContratoDeDividaResponseDTO from(ContratoDeDivida entity) {
        ContratoDeDividaResponseDTO dto = new ContratoDeDividaResponseDTO();
        dto.id         = entity.getId();
        dto.valor      = entity.getValor();
        dto.credor     = UsuarioResumoDTO.from(entity.getCredor());
        dto.devedor    = UsuarioResumoDTO.from(entity.getDevedor());
        dto.viagemId   = entity.getViagem() != null ? entity.getViagem().getId() : null;
        dto.nomeViagem = entity.getViagem() != null ? entity.getViagem().getNome() : null;
        return dto;
    }

    public Long getId()                   { return id; }
    public BigDecimal getValor()          { return valor; }
    public UsuarioResumoDTO getCredor()   { return credor; }
    public UsuarioResumoDTO getDevedor()  { return devedor; }
    public Long getViagemId()             { return viagemId; }
    public String getNomeViagem()         { return nomeViagem; }
}