package br.com.fiap.oficina.api.contract

import br.com.fiap.oficina.funcionario.application.dto.FuncionarioRequest
import br.com.fiap.oficina.funcionario.application.dto.FuncionarioResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping

@Tag(name = "Funcionários", description = "Operações relacionadas ao gerenciamento de funcionários")
@RequestMapping("/api/v2/funcionarios")
interface FuncionarioApi {
    @PostMapping
    @Operation(summary = "Criar um novo funcionário", description = "Cadastra um novo funcionário no sistema")
    fun cadastrar(
        @Valid @RequestBody request: FuncionarioRequest,
    ): ResponseEntity<FuncionarioResponse>

    @GetMapping
    @Operation(
        summary = "Listar todos os funcionários",
        description = "Retorna uma lista com todos os funcionários cadastrados",
    )
    fun listarTodos(): List<FuncionarioResponse>

    @GetMapping("/id/{id}")
    @Operation(
        summary = "Buscar funcionário por ID",
        description = "Busca um funcionário através do seu identificador único",
    )
    fun buscarPorId(
        @PathVariable id: String,
    ): FuncionarioResponse

    @GetMapping("/nome/{nome}")
    @Operation(summary = "Buscar funcionário por nome", description = "Busca um funcionário através do seu nome")
    fun buscarPorNome(
        @Parameter(description = "Nome do funcionário", required = true, example = "Vini Jr.")
        @PathVariable nome: String,
    ): FuncionarioResponse

    @PutMapping("/{id}")
    @Operation(
        summary = "Alterar dados de um funcionário",
        description = "Atualiza as informações de um funcionário existente",
    )
    fun alterar(
        @PathVariable id: String,
        @Valid @RequestBody request: FuncionarioRequest,
    ): FuncionarioResponse

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Deletar um funcionário",
        description = "Remove um funcionário do sistema através do seu identificador único",
    )
    fun deletar(
        @PathVariable id: String,
    )
}
