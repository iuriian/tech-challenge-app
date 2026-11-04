package br.com.fiap.oficina.ordemservico.domain

import br.com.fiap.oficina.shared.domain.AggregateId
import java.util.UUID

/**
 * Referência ao item de catálogo (peça ou serviço) que originou um item do orçamento.
 */
data class ItemReferenciaRef(override val value: UUID) : AggregateId() {
    companion object {
        fun toUUID(uuid: String): ItemReferenciaRef = ItemReferenciaRef(parse(uuid))
    }
}
