package br.com.fiap.oficina.api.contract

import br.com.fiap.oficina.ordemservico.application.dto.AlterarStatusRequest
import br.com.fiap.oficina.ordemservico.application.dto.OrcamentoResponse
import br.com.fiap.oficina.ordemservico.application.dto.OrdemServicoRequest
import br.com.fiap.oficina.ordemservico.application.dto.OrdemServicoResponse
import br.com.fiap.oficina.ordemservico.application.dto.TempoMedioExecucaoResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.annotation.security.RolesAllowed
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping

@RequestMapping("/api/v2/servicos")
@Tag(name = "Ordens de Serviço", description = "Operações relacionadas ao gerenciamento de ordens de serviço")
interface OrdemServicoApi {
    @PostMapping
    @RolesAllowed("ATENDENTE", "ADMIN")
    @Operation(summary = "Criar ordem de serviço", description = "Cadastra uma nova ordem de serviço")
    fun criar(@Valid @RequestBody request: OrdemServicoRequest): ResponseEntity<OrdemServicoResponse>

    @GetMapping
    @RolesAllowed("ATENDENTE", "ADMIN")
    @Operation(summary = "Listar ordens de serviço", description = "Lista todas as ordens de serviço cadastradas")
    fun listarTodos(): List<OrdemServicoResponse>

    @GetMapping("/{id}")
    @RolesAllowed("ATENDENTE", "ADMIN", "CLIENTE")
    @Operation(
        summary = "Buscar ordem de serviço por ID",
        description = "Busca uma ordem de serviço pelo seu identificador único",
    )
    fun buscarPorId(
        @Parameter(description = "ID da ordem de serviço", required = true)
        @PathVariable id: String,
    ): OrdemServicoResponse

    @GetMapping("/cliente/{clienteId}")
    @RolesAllowed("ATENDENTE", "ADMIN", "CLIENTE")
    @Operation(
        summary = "Listar ordens de serviço por cliente",
        description = "Lista as ordens de serviço associadas a um cliente",
    )
    fun listarPorCliente(
        @Parameter(description = "ID do cliente", required = true)
        @PathVariable clienteId: String,
    ): List<OrdemServicoResponse>

    @GetMapping("/{id}/orcamento")
    @RolesAllowed("ATENDENTE", "ADMIN", "CLIENTE")
    @Operation(
        summary = "Obter orçamento da ordem de serviço",
        description = "Retorna o orçamento associado à ordem de serviço",
    )
    fun obterOrcamento(
        @Parameter(description = "ID da ordem de serviço", required = true)
        @PathVariable id: String,
    ): OrcamentoResponse

    @GetMapping("/metricas/tempo-medio")
    @RolesAllowed("ATENDENTE", "ADMIN")
    @Operation(
        summary = "Tempo médio de execução",
        description = "Retorna métricas de tempo das ordens de serviço finalizadas",
    )
    fun tempoMedioExecucao(): TempoMedioExecucaoResponse

    @PutMapping("/{id}")
    @RolesAllowed("ATENDENTE", "ADMIN")
    @Operation(
        summary = "Atualizar ordem de serviço",
        description = "Atualiza os dados de uma ordem de serviço existente",
    )
    fun atualizar(
        @Parameter(description = "ID da ordem de serviço a ser atualizada", required = true)
        @PathVariable id: String,
        @Valid @RequestBody request: OrdemServicoRequest,
    ): OrdemServicoResponse

    @PatchMapping("/{id}/avancar")
    @RolesAllowed("ATENDENTE", "ADMIN", "MECANICO")
    @Operation(
        summary = "Avançar status da ordem de serviço",
        description = "Move a ordem de serviço para o próximo status permitido",
    )
    fun avancarStatus(
        @Parameter(description = "ID da ordem de serviço", required = true)
        @PathVariable id: String,
    ): OrdemServicoResponse

    @PatchMapping("/{id}/status")
    @RolesAllowed("ATENDENTE", "ADMIN", "CLIENTE")
    @Operation(
        summary = "Alterar status da ordem de serviço",
        description = "Altera o status da ordem de serviço respeitando as transições permitidas",
    )
    fun alterarStatus(
        @Parameter(description = "ID da ordem de serviço", required = true)
        @PathVariable id: String,
        @Valid @RequestBody request: AlterarStatusRequest,
    ): OrdemServicoResponse

    @DeleteMapping("/{id}")
    @RolesAllowed("ATENDENTE", "ADMIN")
    @Operation(
        summary = "Excluir ordem de serviço",
        description = "Exclui uma ordem de serviço do sistema pelo seu ID",
    )
    fun deletar(
        @Parameter(description = "ID da ordem de serviço a ser excluída", required = true)
        @PathVariable id: String,
    )
}
