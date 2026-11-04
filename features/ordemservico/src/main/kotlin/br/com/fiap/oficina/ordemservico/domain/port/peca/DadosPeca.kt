package br.com.fiap.oficina.ordemservico.domain.port.peca

import br.com.fiap.oficina.ordemservico.domain.ItemReferenciaRef
import java.math.BigDecimal

data class DadosPeca(
    val id: ItemReferenciaRef,
    val codigo: String,
    val nome: String,
    val precoVenda: BigDecimal,
    val ativo: Boolean,
)
