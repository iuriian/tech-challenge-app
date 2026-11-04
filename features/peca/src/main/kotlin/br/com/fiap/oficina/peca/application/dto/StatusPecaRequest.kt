package br.com.fiap.oficina.peca.application.dto

import jakarta.validation.constraints.NotNull

data class StatusPecaRequest(
    @field:NotNull
    val ativo: Boolean = false,
)
