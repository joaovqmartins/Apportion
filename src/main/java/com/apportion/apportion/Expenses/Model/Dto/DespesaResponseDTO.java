package com.apportion.apportion.Expenses.Model.Dto;

import com.apportion.apportion.Expenses.Model.DespesasEntity;
import com.apportion.apportion.Identity.Model.Entidades.Dto.Requests.UsuarioResumoDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class DespesaResponseDTO {

    private Long id;
    private String descricao;
    private BigDecimal valor;
    private UsuarioResumoDTO recebedor;
    private List<UsuarioResumoDTO> pagantes;
    private Long viagemId;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dataCriacao;

    // -----------------------------------------------------------------------
    // Factory method — converte a entidade JPA em DTO
    // -----------------------------------------------------------------------

    public static DespesaResponseDTO from(DespesasEntity entity) {
        DespesaResponseDTO dto = new DespesaResponseDTO();
        dto.id           = entity.getId();
        dto.descricao    = entity.getDescricao();
        dto.valor        = entity.getValor();
        dto.viagemId     = entity.getViagem() != null ? entity.getViagem().getId() : null;
        dto.recebedor    = UsuarioResumoDTO.from(entity.getRecebedor());
        dto.pagantes     = entity.getPagantes()
                .stream()
                .map(UsuarioResumoDTO::from)
                .collect(Collectors.toList());
        return dto;
    }

}
