package br.com.fiap.oficina.ordemservico.application.dto

import br.com.fiap.oficina.ordemservico.domain.TipoItemOrcamento
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.math.BigDecimal

data class ItemOrcamentoRequest(
    @field:NotNull
    val tipo: TipoItemOrcamento,
    @field:NotNull
    val referenciaId: String,
    @field:NotNull
    @field:Positive
    val quantidade: BigDecimal,
)
