package br.com.fiap.oficina.ordemservico.application.dto

import br.com.fiap.oficina.ordemservico.domain.TipoItemOrcamento
import java.math.BigDecimal
import java.util.UUID

data class ItemOrcamentoResponse(
    val tipo: TipoItemOrcamento,
    val referenciaId: UUID,
    val codigoReferencia: String?,
    val descricao: String,
    val valorUnitario: BigDecimal,
    val quantidade: BigDecimal,
    val subtotal: BigDecimal,
)
