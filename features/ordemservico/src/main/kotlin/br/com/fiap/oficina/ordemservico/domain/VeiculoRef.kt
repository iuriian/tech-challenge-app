package br.com.fiap.oficina.ordemservico.domain

import br.com.fiap.oficina.shared.domain.AggregateId
import java.util.UUID

data class VeiculoRef(override val value: UUID) : AggregateId() {
    companion object {
        fun toUUID(uuid: String): VeiculoRef = VeiculoRef(parse(uuid))
    }
}
