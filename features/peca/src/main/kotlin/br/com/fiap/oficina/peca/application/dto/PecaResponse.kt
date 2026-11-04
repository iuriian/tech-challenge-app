package br.com.fiap.oficina.peca.application.dto

import java.math.BigDecimal

data class PecaResponse(
    val id: String,
    val codigo: String,
    val nome: String,
    val descricao: String? = null,
    val fabricante: String,
    val fornecedor: String? = null,
    val precoDeCompra: BigDecimal,
    val precoDeVenda: BigDecimal,
    val qtdEstoque: Int,
    val ativo: Boolean,
)
