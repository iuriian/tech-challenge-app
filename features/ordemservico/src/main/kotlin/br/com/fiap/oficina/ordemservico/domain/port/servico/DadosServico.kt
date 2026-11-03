package br.com.fiap.oficina.ordemservico.domain.port.servico

import br.com.fiap.oficina.ordemservico.domain.ItemReferenciaRef
import java.math.BigDecimal

data class DadosServico(val id: ItemReferenciaRef, val descricao: String, val valor: BigDecimal, val ativo: Boolean)
