package br.com.fiap.oficina.api.exception

import io.swagger.v3.oas.annotations.media.Schema
import java.time.Instant

@Schema(name = "ValidationErrorResponse", description = "Erro de validação com detalhe por campo")
data class ValidationErrorResponse(
    @field:Schema(example = "2026-10-27T13:45:30Z")
    val timestamp: Instant = Instant.now(),
    @field:Schema(example = "400")
    val status: Int,
    @field:Schema(example = "Dados inválidos")
    val error: String,
    @field:Schema(example = "Os dados fornecidos são inválidos")
    val message: String,
    @field:Schema(example = "/api/v2/funcionarios")
    val path: String,
    @field:Schema(
        description = "Mensagem de erro por campo",
        example = """{"nome":"não deve estar em branco","cpf":"CPF inválido"}""",
    )
    val fields: Map<String, String>,
)
