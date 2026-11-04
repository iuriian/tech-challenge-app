package br.com.fiap.oficina.ordemservico.domain

import br.com.fiap.oficina.shared.domain.AggregateId
import java.util.UUID

data class OrdemServicoId(override val value: UUID) : AggregateId() {
    companion object {
        fun generate(): OrdemServicoId = OrdemServicoId(newId())

        fun toUUID(uuid: String): OrdemServicoId = OrdemServicoId(parse(uuid))
    }
}
