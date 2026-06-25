package com.apportion.apportion.Expenses.Controller;

import com.apportion.apportion.Expenses.Model.Dto.DespesaResponseDTO;
import com.apportion.apportion.Expenses.Service.DespesaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/despesas")
@Validated
@Tag(name = "Despesas", description = "Endpoints para gerenciamento de despesas")
public class DespesaController {

    @Autowired
    private DespesaService despesaService;

    /**
     * GET /despesas/viagem/{viagemId}
     * Retorna todas as despesas de uma viagem, paginadas.
     *
     * Exemplo de requisição:
     *   GET /despesas/viagem/1?page=0&size=10&sort=dataCriacao,desc
     *
     * Exemplo de resposta (200 OK):
     * {
     *   "content": [
     *     {
     *       "id": 1,
     *       "descricao": "Jantar",
     *       "valor": 150.00,
     *       "recebedor": { "id": 2, "nome": "Maria" },
     *       "pagantes": [{ "id": 3, "nome": "João" }],
     *       "dataCriacao": "2024-01-15T20:00:00"
     *     }
     *   ],
     *   "totalElements": 1,
     *   "totalPages": 1,
     *   "number": 0,
     *   "size": 10
     * }
     */
    @Operation(
            summary = "Buscar despesas por viagem",
            description = "Retorna todas as despesas associadas a uma viagem específica, com suporte a paginação."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Despesas retornadas com sucesso"),
            @ApiResponse(responseCode = "404", description = "Viagem não encontrada"),
            @ApiResponse(responseCode = "400", description = "Parâmetros de paginação inválidos")
    })
    @GetMapping("/viagem/{viagemId}")
    public ResponseEntity<Page<DespesaResponseDTO>> findByViagem(
            @Parameter(description = "ID da viagem", required = true)
            @PathVariable @Positive Long viagemId,

            @Parameter(description = "Número da página (começa em 0)")
            @RequestParam(defaultValue = "0") @PositiveOrZero int page,

            @Parameter(description = "Quantidade de itens por página")
            @RequestParam(defaultValue = "10") @Positive int size,

            @Parameter(description = "Campo de ordenação (ex: dataCriacao,desc)")
            @RequestParam(defaultValue = "dataCriacao,desc") String sort) {

        String[] sortParams = sort.split(",");
        Sort.Direction direction = sortParams.length > 1
                ? Sort.Direction.fromString(sortParams[1])
                : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParams[0]));
        Page<DespesaResponseDTO> despesas = despesaService.findByViagem(viagemId, pageable);
        return ResponseEntity.ok(despesas);
    }

    /**
     * GET /despesas/usuario/{userId}
     * Retorna todas as despesas em que o usuário é recebedor ou pagante, paginadas.
     *
     * Exemplo de requisição:
     *   GET /despesas/usuario/3?page=0&size=10
     *
     * Exemplo de resposta (200 OK):
     * {
     *   "content": [
     *     {
     *       "id": 2,
     *       "descricao": "Hospedagem",
     *       "valor": 300.00,
     *       "recebedor": { "id": 3, "nome": "João" },
     *       "pagantes": [{ "id": 2, "nome": "Maria" }],
     *       "dataCriacao": "2024-01-16T10:00:00"
     *     }
     *   ],
     *   "totalElements": 1,
     *   "totalPages": 1,
     *   "number": 0,
     *   "size": 10
     * }
     */
    @Operation(
            summary = "Buscar despesas do usuário",
            description = "Retorna todas as despesas em que o usuário participa (como recebedor ou pagante), com suporte a paginação."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Despesas retornadas com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos")
    })
    @GetMapping("/usuario/{userId}")
    public ResponseEntity<Page<DespesaResponseDTO>> findByUser(
            @Parameter(description = "ID do usuário", required = true)
            @PathVariable @Positive Long userId,

            @RequestParam(defaultValue = "0") @PositiveOrZero int page,
            @RequestParam(defaultValue = "10") @Positive int size,
            @RequestParam(defaultValue = "dataCriacao,desc") String sort) {

        String[] sortParams = sort.split(",");
        Sort.Direction direction = sortParams.length > 1
                ? Sort.Direction.fromString(sortParams[1])
                : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParams[0]));
        Page<DespesaResponseDTO> despesas = despesaService.findByUser(userId, pageable);
        return ResponseEntity.ok(despesas);
    }
}
