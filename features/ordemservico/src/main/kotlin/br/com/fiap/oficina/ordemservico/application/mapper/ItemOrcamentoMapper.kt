package br.com.fiap.oficina.ordemservico.application.mapper

import br.com.fiap.oficina.ordemservico.application.dto.ItemOrcamentoResponse
import br.com.fiap.oficina.ordemservico.domain.ItemOrcamento
import org.springframework.stereotype.Component

@Component
internal class ItemOrcamentoMapper {
    fun toResponse(item: ItemOrcamento): ItemOrcamentoResponse = ItemOrcamentoResponse(
        tipo = item.tipo,
        referenciaId = item.referenciaId.value,
        codigoReferencia = item.codigoReferencia,
        descricao = item.descricao,
        valorUnitario = item.valorUnitario,
        quantidade = item.quantidade,
        subtotal = item.subtotal,
    )
}
