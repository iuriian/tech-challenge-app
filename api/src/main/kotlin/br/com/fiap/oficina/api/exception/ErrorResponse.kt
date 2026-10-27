package br.com.fiap.oficina.api.exception

import io.swagger.v3.oas.annotations.media.Schema
import java.time.Instant

@Schema(name = "ErrorResponse", description = "Corpo padrão de erro da API")
data class ErrorResponse(
    @field:Schema(description = "Momento do erro", example = "2026-10-27T13:45:30Z")
    val timestamp: Instant = Instant.now(),
    @field:Schema(description = "Código", example = "404")
    val status: Int,
    @field:Schema(description = "Categoria", example = "Recurso não encontrado")
    val error: String,
    @field:Schema(description = "Detalhe", example = "Funcionário não encontrado, id: 7f3a...")
    val message: String,
    @field:Schema(description = "Caminho", example = "/api/v2/funcionarios/id/7f3a...")
    val path: String,
)
