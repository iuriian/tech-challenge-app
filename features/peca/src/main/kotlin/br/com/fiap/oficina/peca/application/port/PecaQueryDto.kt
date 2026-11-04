package br.com.fiap.oficina.peca.application.port

import java.math.BigDecimal

data class PecaQueryDto(
    val id: String,
    val codigo: String,
    val nome: String,
    val precoVenda: BigDecimal,
    val ativo: Boolean,
)
