package br.com.fiap.oficina.ordemservico.application.dto

import java.math.BigDecimal

data class OrcamentoResponse(val itens: List<ItemOrcamentoResponse>, val valorTotal: BigDecimal)
