package com.apportion.apportion.Expenses.Controller;

import com.apportion.apportion.Expenses.Model.Dto.ContratoDeDividaResponseDTO;
import com.apportion.apportion.Expenses.Service.ContratoDeDividaService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dividas")
@Validated
@Tag(name = "Dívidas", description = "Endpoints para consulta de contratos de dívida entre usuários")
public class ContratoDeDividaController {

    @Autowired
    private ContratoDeDividaService contratoDeDividaService;

    /**
     * GET /dividas/viagem-ativa/usuario/{userId}
     * Retorna as dívidas da viagem ativa do usuário.
     *
     * Exemplo de requisição:
     *   GET /dividas/viagem-ativa/usuario/3?page=0&size=10
     *
     * Exemplo de resposta (200 OK):
     * {
     *   "content": [
     *     {
     *       "id": 1,
     *       "valor": 75.00,
     *       "credor": { "id": 2, "nome": "Maria" },
     *       "devedor": { "id": 3, "nome": "João" },
     *       "viagemId": 10,
     *       "nomeViagem": "Europa 2024"
     *     }
     *   ],
     *   "totalElements": 1,
     *   "totalPages": 1,
     *   "number": 0,
     *   "size": 10
     * }
     */
    @Operation(
            summary = "Buscar dívidas da viagem ativa do usuário",
            description = "Retorna todas as dívidas (como credor ou devedor) na viagem com status ATIVA do usuário informado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dívidas retornadas com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "400", description = "ID inválido")
    })
    @GetMapping("/viagem-ativa/usuario/{userId}")
    public ResponseEntity<Page<ContratoDeDividaResponseDTO>> findByViagemAtivaUser(
            @Parameter(description = "ID do usuário", required = true)
            @PathVariable @Positive Long userId,

            @RequestParam(defaultValue = "0") @PositiveOrZero int page,
            @RequestParam(defaultValue = "10") @Positive int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(
                contratoDeDividaService.findByViagemAtivaUser(userId, pageable));
    }

    /**
     * GET /dividas/viagem/{viagemId}
     * Retorna todas as dívidas de uma viagem específica.
     *
     * Exemplo de requisição:
     *   GET /dividas/viagem/10?page=0&size=20
     *
     * Exemplo de resposta (200 OK):
     * {
     *   "content": [
     *     {
     *       "id": 1,
     *       "valor": 75.00,
     *       "credor": { "id": 2, "nome": "Maria" },
     *       "devedor": { "id": 3, "nome": "João" },
     *       "viagemId": 10,
     *       "nomeViagem": "Europa 2024"
     *     },
     *     {
     *       "id": 2,
     *       "valor": 120.00,
     *       "credor": { "id": 4, "nome": "Ana" },
     *       "devedor": { "id": 3, "nome": "João" },
     *       "viagemId": 10,
     *       "nomeViagem": "Europa 2024"
     *     }
     *   ],
     *   "totalElements": 2,
     *   "totalPages": 1,
     *   "number": 0,
     *   "size": 20
     * }
     */
    @Operation(
            summary = "Buscar dívidas por viagem",
            description = "Retorna todas as dívidas de uma viagem específica, com suporte a paginação."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dívidas retornadas com sucesso"),
            @ApiResponse(responseCode = "404", description = "Viagem não encontrada")
    })
    @GetMapping("/viagem/{viagemId}")
    public ResponseEntity<Page<ContratoDeDividaResponseDTO>> findByViagem(
            @Parameter(description = "ID da viagem", required = true)
            @PathVariable @Positive Long viagemId,

            @RequestParam(defaultValue = "0") @PositiveOrZero int page,
            @RequestParam(defaultValue = "10") @Positive int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(
                contratoDeDividaService.findByViagem(viagemId, pageable));
    }

    /**
     * GET /dividas/viagens-concluidas/usuario/{userId}
     * Retorna dívidas de todas as viagens CONCLUÍDAS do usuário.
     *
     * Exemplo de requisição:
     *   GET /dividas/viagens-concluidas/usuario/3?page=0&size=10
     *
     * Exemplo de resposta (200 OK):
     * {
     *   "content": [
     *     {
     *       "id": 5,
     *       "valor": 200.00,
     *       "credor": { "id": 3, "nome": "João" },
     *       "devedor": { "id": 2, "nome": "Maria" },
     *       "viagemId": 7,
     *       "nomeViagem": "Portugal 2023"
     *     }
     *   ],
     *   "totalElements": 1,
     *   "totalPages": 1,
     *   "number": 0,
     *   "size": 10
     * }
     */
    @Operation(
            summary = "Buscar dívidas de viagens concluídas do usuário",
            description = "Retorna todas as dívidas (como credor ou devedor) em viagens com status CONCLUIDA do usuário informado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dívidas retornadas com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @GetMapping("/viagens-concluidas/usuario/{userId}")
    public ResponseEntity<Page<ContratoDeDividaResponseDTO>> findByViagemConcluidaUser(
            @Parameter(description = "ID do usuário", required = true)
            @PathVariable @Positive Long userId,

            @RequestParam(defaultValue = "0") @PositiveOrZero int page,
            @RequestParam(defaultValue = "10") @Positive int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(
                contratoDeDividaService.findByViagemConcluidaUser(userId, pageable));
    }
    /**
     * GET /dividas/ativas/credor/{userId}
     * Retorna as dívidas ATIVAS onde o usuário é o CREDOR (tem a receber).
     *
     * Exemplo de requisição:
     * GET /dividas/ativas/credor/3?page=0&size=10
     */
    @Operation(
            summary = "Buscar dívidas ativas como credor",
            description = "Retorna todas as dívidas ativas (ativa = true) onde o usuário informado é o credor (tem a receber)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dívidas retornadas com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "400", description = "ID inválido")
    })
    @GetMapping("/ativas/credor/{userId}")
    public ResponseEntity<Page<ContratoDeDividaResponseDTO>> findDividasAtivasComoCredor(
            @Parameter(description = "ID do usuário (credor)", required = true)
            @PathVariable @Positive Long userId,

            @RequestParam(defaultValue = "0") @PositiveOrZero int page,
            @RequestParam(defaultValue = "10") @Positive int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(
                contratoDeDividaService.findDividasAtivasComoCredor(userId, pageable));
    }

    /**
     * GET /dividas/concluidas/credor/{userId}
     * Retorna as dívidas CONCLUÍDAS onde o usuário era o CREDOR (tinha a receber).
     *
     * Exemplo de requisição:
     * GET /dividas/concluidas/credor/3?page=0&size=10
     */
    @Operation(
            summary = "Buscar dívidas concluídas como credor",
            description = "Retorna todas as dívidas concluídas (ativa = false) onde o usuário informado era o credor."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dívidas retornadas com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "400", description = "ID inválido")
    })
    @GetMapping("/concluidas/credor/{userId}")
    public ResponseEntity<Page<ContratoDeDividaResponseDTO>> findDividasConcluidasComoCredor(
            @Parameter(description = "ID do usuário (credor)", required = true)
            @PathVariable @Positive Long userId,

            @RequestParam(defaultValue = "0") @PositiveOrZero int page,
            @RequestParam(defaultValue = "10") @Positive int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(
                contratoDeDividaService.findDividasConcluidasComoCredor(userId, pageable));
    }

    /**
     * GET /dividas/ativas/devedor/{userId}
     * Retorna as dívidas ATIVAS onde o usuário é o DEVEDOR (tem a pagar).
     *
     * Exemplo de requisição:
     * GET /dividas/ativas/devedor/3?page=0&size=10
     */
    @Operation(
            summary = "Buscar dívidas ativas como devedor",
            description = "Retorna todas as dívidas ativas (ativa = true) onde o usuário informado é o devedor (tem a pagar)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dívidas retornadas com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "400", description = "ID inválido")
    })
    @GetMapping("/ativas/devedor/{userId}")
    public ResponseEntity<Page<ContratoDeDividaResponseDTO>> findDividasAtivasComoDevedor(
            @Parameter(description = "ID do usuário (devedor)", required = true)
            @PathVariable @Positive Long userId,

            @RequestParam(defaultValue = "0") @PositiveOrZero int page,
            @RequestParam(defaultValue = "10") @Positive int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(
                contratoDeDividaService.findDividasAtivasComoDevedor(userId, pageable));
    }

    /**
     * GET /dividas/concluidas/devedor/{userId}
     * Retorna as dívidas CONCLUÍDAS onde o usuário era o DEVEDOR (tinha a pagar).
     *
     * Exemplo de requisição:
     * GET /dividas/concluidas/devedor/3?page=0&size=10
     */
    @Operation(
            summary = "Buscar dívidas concluídas como devedor",
            description = "Retorna todas as dívidas concluídas (ativa = false) onde o usuário informado era o devedor."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dívidas retornadas com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "400", description = "ID inválido")
    })
    @GetMapping("/concluidas/devedor/{userId}")
    public ResponseEntity<Page<ContratoDeDividaResponseDTO>> findDividasConcluidasComoDevedor(
            @Parameter(description = "ID do usuário (devedor)", required = true)
            @PathVariable @Positive Long userId,

            @RequestParam(defaultValue = "0") @PositiveOrZero int page,
            @RequestParam(defaultValue = "10") @Positive int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(
                contratoDeDividaService.findDividasConcluidasComoDevedor(userId, pageable));
    }
}
