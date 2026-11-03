package br.com.fiap.oficina.ordemservico.application.dto

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank

data class OrdemServicoRequest(
    @field:NotBlank
    val descricao: String,
    @field:NotBlank
    val funcionarioId: String,
    @field:NotBlank
    val clienteId: String,
    @field:NotBlank
    val veiculoId: String,
    @field:Valid
    val itens: List<ItemOrcamentoRequest> = emptyList(),
)
