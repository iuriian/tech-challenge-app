package br.com.fiap.oficina.api.contract

import br.com.fiap.oficina.veiculo.application.dto.VeiculoRequest
import br.com.fiap.oficina.veiculo.application.dto.VeiculoResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.annotation.security.RolesAllowed
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping

@RequestMapping("/veiculos")
@Tag(name = "Veículos", description = "Operações relacionadas ao gerenciamento de veículos")
interface VeiculoApi {
    @PostMapping
    @RolesAllowed("ATENDENTE", "ADMIN")
    @Operation(summary = "Criar um novo veículo", description = "Cadastra um novo veículo no sistema")
    fun criar(@Valid @RequestBody request: VeiculoRequest): ResponseEntity<VeiculoResponse>

    @GetMapping("/{id}")
    @RolesAllowed("ATENDENTE", "ADMIN")
    @Operation(summary = "Buscar veículo por ID", description = "Busca um veículo pelo seu ID único")
    fun buscarVeiculoPorId(
        @Parameter(description = "ID do veículo", required = true)
        @PathVariable id: String,
    ): VeiculoResponse

    @GetMapping("/placa/{placa}")
    @RolesAllowed("ATENDENTE", "ADMIN")
    @Operation(summary = "Buscar veículo pela placa", description = "Busca um veículo pela sua placa")
    fun buscarVeiculoPorPlaca(
        @Parameter(description = "Placa do veículo", required = true, example = "abc1234")
        @PathVariable placa: String,
    ): VeiculoResponse

    @GetMapping("/motorista/{motoristaId}")
    @RolesAllowed("ATENDENTE", "ADMIN")
    @Operation(summary = "Buscar veículos por motorista", description = "Busca veículos cadastrados para um cliente")
    fun buscarVeiculosPorProprietario(
        @Parameter(description = "ID do cliente (motorista)", required = true)
        @PathVariable proprietarioId: String,
    ): List<VeiculoResponse>

    @GetMapping
    @RolesAllowed("ATENDENTE", "ADMIN")
    @Operation(summary = "Listar veículos", description = "Lista todos os veículos cadastrados no sistema")
    fun listarTodos(): List<VeiculoResponse>

    @PutMapping("/{id}")
    @RolesAllowed("ATENDENTE", "ADMIN")
    @Operation(summary = "Atualizar um veículo", description = "Atualiza os dados de um veículo existente")
    fun atualizar(
        @Parameter(description = "ID do veículo a ser atualizado", required = true)
        @PathVariable id: String,
        @Valid @RequestBody request: VeiculoRequest,
    ): VeiculoResponse

    @DeleteMapping("/{id}")
    @RolesAllowed("ADMIN")
    @Operation(summary = "Remover um veículo", description = "Exclui um veículo do sistema pelo seu ID")
    fun remover(
        @Parameter(description = "ID do veículo a ser removido", required = true)
        @PathVariable id: String,
    )
}
