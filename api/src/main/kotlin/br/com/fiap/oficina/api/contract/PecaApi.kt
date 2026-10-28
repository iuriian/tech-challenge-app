package br.com.fiap.oficina.api.contract

import br.com.fiap.oficina.peca.application.dto.PecaRequest
import br.com.fiap.oficina.peca.application.dto.PecaResponse
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
import org.springframework.web.bind.annotation.RequestParam

@RequestMapping("/api/v2/pecas")
@Tag(name = "Peças", description = "Operações relacionadas ao gerenciamento de peças e estoque")
interface PecaApi {
    @PostMapping
    @RolesAllowed("ADMIN")
    @Operation(
        summary = "Criar uma nova peça",
        description = "Cadastra uma nova peça no sistema. Retorna conflito (409) se o código já existir.",
    )
    fun cadastrar(@Valid @RequestBody request: PecaRequest): ResponseEntity<PecaResponse>

    @PutMapping("/{codigo}")
    @RolesAllowed("ADMIN")
    @Operation(
        summary = "Atualizar uma peça",
        description = "Atualiza os dados de uma peça existente identificada pelo código",
    )
    fun atualizar(
        @Parameter(description = "Código da peça a ser atualizada", required = true, example = "PEC001")
        @PathVariable codigo: String,
        @Valid @RequestBody request: PecaRequest,
    ): PecaResponse

    @DeleteMapping("/{codigo}")
    @RolesAllowed("ADMIN")
    @Operation(
        summary = "Desativar uma peça",
        description = "Desativa (remoção lógica) uma peça identificada pelo código",
    )
    fun desativarPeca(
        @Parameter(description = "Código da peça a ser desativada", required = true, example = "PEC001")
        @PathVariable codigo: String,
    )

    @GetMapping
    @RolesAllowed("ATENDENTE", "ADMIN", "MECANICO")
    @Operation(
        summary = "Listar peças",
        description = "Lista todas as peças cadastradas no sistema",
    )
    fun listar(): List<PecaResponse>

    @GetMapping("/codigo/{codigo}")
    @RolesAllowed("ATENDENTE", "ADMIN", "MECANICO")
    @Operation(
        summary = "Buscar peça por código",
        description = "Busca uma peça pelo seu código único",
    )
    fun buscarPorCodigo(
        @Parameter(description = "Código da peça", required = true, example = "PEC001")
        @PathVariable codigo: String,
    ): PecaResponse

    @PatchMapping("/{codigo}/estoque/retirar")
    @RolesAllowed("ATENDENTE", "ADMIN", "MECANICO")
    @Operation(
        summary = "Retirar peças do estoque",
        description = "Reduz a quantidade em estoque de uma peça pela quantidade informada",
    )
    fun retirarPecas(
        @Parameter(description = "Código da peça", required = true, example = "PEC001")
        @PathVariable codigo: String,
        @Parameter(description = "Quantidade a ser retirada do estoque", required = true, example = "5")
        @RequestParam qtd: Int,
    ): PecaResponse

    @PatchMapping("/{codigo}/estoque/repor")
    @RolesAllowed("ATENDENTE", "ADMIN", "MECANICO")
    @Operation(
        summary = "Repor peças no estoque",
        description = "Aumenta a quantidade em estoque de uma peça pela quantidade informada",
    )
    fun reporPecas(
        @Parameter(description = "Código da peça", required = true, example = "PEC001")
        @PathVariable codigo: String,
        @Parameter(description = "Quantidade a ser reposta no estoque", required = true, example = "10")
        @RequestParam qtd: Int,
    ): PecaResponse

    @PatchMapping("/{codigo}/reativar")
    @RolesAllowed("ADMIN")
    @Operation(
        summary = "Reativar uma peça",
        description = "Reativa uma peça previamente desativada",
    )
    fun reativar(
        @Parameter(description = "Código da peça a ser reativada", required = true, example = "PEC001")
        @PathVariable codigo: String,
    ): PecaResponse
}
