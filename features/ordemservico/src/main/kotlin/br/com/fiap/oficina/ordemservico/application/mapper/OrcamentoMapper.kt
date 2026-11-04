package br.com.fiap.oficina.ordemservico.application.mapper

import br.com.fiap.oficina.ordemservico.application.dto.OrcamentoResponse
import br.com.fiap.oficina.ordemservico.domain.Orcamento
import org.springframework.stereotype.Component

@Component
internal class OrcamentoMapper(private val itemOrcamentoMapper: ItemOrcamentoMapper) {
    fun toResponse(orcamento: Orcamento): OrcamentoResponse = OrcamentoResponse(
        itens = orcamento.itens.map(itemOrcamentoMapper::toResponse),
        valorTotal = orcamento.valorTotal,
    )
}
