package br.com.fiap.oficina.api.contract

import br.com.fiap.oficina.servico.application.dto.ServicoRequest
import br.com.fiap.oficina.servico.application.dto.ServicoResponse
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

@RequestMapping("/api/v2/catalogo/servicos")
@Tag(name = "Catálogo de Serviços", description = "Operações relacionadas ao catálogo de serviços da oficina")
interface ServicoApi {
    @PostMapping
    @RolesAllowed("ADMIN")
    @Operation(summary = "Criar serviço", description = "Cadastra um novo serviço no catálogo da oficina")
    fun criar(@Valid @RequestBody request: ServicoRequest): ResponseEntity<ServicoResponse>

    @GetMapping
    @RolesAllowed("ATENDENTE", "ADMIN", "MECANICO")
    @Operation(summary = "Listar serviços ativos", description = "Lista os serviços ativos disponíveis no catálogo")
    fun listarAtivos(): List<ServicoResponse>

    @GetMapping("/todos")
    @RolesAllowed("ADMIN")
    @Operation(
        summary = "Listar todos os serviços",
        description = "Lista serviços ativos e inativos para administração do catálogo",
    )
    fun listarTodos(): List<ServicoResponse>

    @GetMapping("/{id}")
    @RolesAllowed("ATENDENTE", "ADMIN", "MECANICO")
    @Operation(summary = "Buscar serviço", description = "Busca um serviço do catálogo pelo seu identificador único")
    fun buscar(
        @Parameter(description = "ID do serviço", required = true)
        @PathVariable id: String,
    ): ServicoResponse

    @PutMapping("/{id}")
    @RolesAllowed("ADMIN")
    @Operation(summary = "Atualizar serviço", description = "Atualiza a descrição e o valor de um serviço do catálogo")
    fun atualizar(
        @Parameter(description = "ID do serviço a ser atualizado", required = true)
        @PathVariable id: String,
        @Valid @RequestBody request: ServicoRequest,
    ): ServicoResponse

    @DeleteMapping("/{id}")
    @RolesAllowed("ADMIN")
    @Operation(summary = "Desativar serviço", description = "Desativa (remoção lógica) um serviço do catálogo")
    fun desativar(
        @Parameter(description = "ID do serviço a ser desativado", required = true)
        @PathVariable id: String,
    )

    @PatchMapping("/{id}/reativar")
    @RolesAllowed("ADMIN")
    @Operation(summary = "Reativar serviço", description = "Reativa um serviço previamente desativado")
    fun reativar(
        @Parameter(description = "ID do serviço a ser reativado", required = true)
        @PathVariable id: String,
    ): ServicoResponse
}
