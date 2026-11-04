package br.com.fiap.oficina.ordemservico.infrastructure.mapper

import br.com.fiap.oficina.ordemservico.domain.ItemOrcamento
import br.com.fiap.oficina.ordemservico.domain.ItemReferenciaRef
import br.com.fiap.oficina.ordemservico.infrastructure.persistence.ItemOrcamentoJPA
import br.com.fiap.oficina.shared.domain.mapper.JPAMapper

internal class ItemOrcamentoJPAMapper : JPAMapper<ItemOrcamento, ItemOrcamentoJPA> {
    override fun toDomain(jpa: ItemOrcamentoJPA): ItemOrcamento = ItemOrcamento(
        tipo = jpa.tipo,
        referenciaId = ItemReferenciaRef(jpa.referenciaId),
        descricao = jpa.descricao,
        valorUnitario = jpa.valorUnitario,
        quantidade = jpa.quantidade,
        codigoReferencia = jpa.codigoReferencia,
    )

    override fun toJPA(domain: ItemOrcamento): ItemOrcamentoJPA = ItemOrcamentoJPA(
        tipo = domain.tipo,
        referenciaId = domain.referenciaId.value,
        descricao = domain.descricao,
        valorUnitario = domain.valorUnitario,
        quantidade = domain.quantidade,
        codigoReferencia = domain.codigoReferencia,
    )
}
