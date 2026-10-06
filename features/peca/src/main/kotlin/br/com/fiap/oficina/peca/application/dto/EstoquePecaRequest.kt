package br.com.fiap.oficina.peca.application.dto

import jakarta.validation.constraints.NotNull

data class EstoquePecaRequest(
    @field:NotNull
    val quantidade: Int,
)
