package br.com.fiap.oficina.api.contract

import br.com.fiap.oficina.cliente.application.dto.ClienteRequest
import br.com.fiap.oficina.cliente.application.dto.ClienteResponse
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

@RequestMapping("/api/v2/clientes")
@Tag(name = "Clientes", description = "Operações relacionadas ao gerenciamento de clientes")
interface ClienteApi {
    @PostMapping
    @RolesAllowed("ATENDENTE", "ADMIN")
    @Operation(summary = "Criar um novo cliente", description = "Cadastra um novo cliente no sistema")
    fun cadastrar(@Valid @RequestBody request: ClienteRequest): ResponseEntity<ClienteResponse>

    @GetMapping
    @RolesAllowed("ATENDENTE", "ADMIN")
    @Operation(summary = "Listar clientes", description = "Lista todos os clientes cadastrados no sistema")
    fun listarTodos(): List<ClienteResponse>

    @GetMapping("/{id}")
    @RolesAllowed("ATENDENTE", "ADMIN")
    @Operation(summary = "Buscar cliente por ID", description = "Busca um cliente através do seu identificador único")
    fun buscarPorId(
        @Parameter(description = "ID do cliente", required = true)
        @PathVariable id: String,
    ): ClienteResponse

    @GetMapping("/nome/{nome}")
    @RolesAllowed("ATENDENTE", "ADMIN")
    @Operation(summary = "Buscar cliente por nome", description = "Busca um cliente através do seu nome")
    fun buscarPorNome(
        @Parameter(description = "Nome do cliente", required = true, example = "João Silva")
        @PathVariable nome: String,
    ): ClienteResponse

    @GetMapping("/documento/{documentoNumero}")
    @RolesAllowed("ATENDENTE", "ADMIN")
    @Operation(
        summary = "Buscar cliente por número de documento",
        description = "Busca um cliente através do número do documento de identificação",
    )
    fun buscarPorDocumento(
        @Parameter(
            description =
            "Número do documento de identificação. " +
                "Deve conter apenas números, sem caracteres especiais (pontos, hífens, etc.)",
            required = true,
            example = "12345678900",
        )
        @PathVariable documentoNumero: String,
    ): ClienteResponse

    @PutMapping("/{id}")
    @RolesAllowed("ATENDENTE", "ADMIN")
    @Operation(summary = "Alterar dados de um cliente", description = "Atualiza as informações de um cliente existente")
    fun alterar(
        @Parameter(description = "ID do cliente a ser alterado", required = true)
        @PathVariable id: String,
        @Valid @RequestBody request: ClienteRequest,
    ): ClienteResponse

    @DeleteMapping("/{id}")
    @RolesAllowed("ADMIN")
    @Operation(summary = "Remover um cliente", description = "Exclui um cliente do sistema através do seu ID")
    fun deletar(
        @Parameter(description = "ID do cliente a ser removido", required = true)
        @PathVariable id: String,
    )
}
