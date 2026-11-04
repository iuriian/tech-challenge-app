package br.com.fiap.oficina.cliente.domain

import br.com.fiap.oficina.shared.domain.AggregateId
import java.util.UUID

data class ClienteId(override val value: UUID) : AggregateId() {
    companion object {
        fun generate(): ClienteId = ClienteId(newId())

        fun toUUID(uuid: String): ClienteId = ClienteId(parse(uuid))
    }
}
