package com.apportion.apportion.Identity.Model.Entidades.Dto.Requests;
import com.apportion.apportion.Identity.Model.Entidades.UsuarioEntity;

/**
 * DTO resumido de usuário, usado como campo embutido em outros DTOs.
 * Evita expor dados sensíveis (senha, e-mail, etc.) do UsuarioEntity.
 */
public class UsuarioResumoDTO {

    private Long id;
    private String nome;

    public static UsuarioResumoDTO from(UsuarioEntity entity) {
        if (entity == null) return null;
        UsuarioResumoDTO dto = new UsuarioResumoDTO();
        dto.id   = entity.getId();
        dto.nome = entity.getNome();
        return dto;
    }

    public Long getId()    { return id; }
    public String getNome() { return nome; }
}