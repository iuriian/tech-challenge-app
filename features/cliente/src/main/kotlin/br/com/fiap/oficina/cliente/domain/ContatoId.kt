package br.com.fiap.oficina.cliente.domain

import br.com.fiap.oficina.shared.domain.AggregateId
import java.util.UUID

data class ContatoId(override val value: UUID) : AggregateId() {
    companion object {
        fun generate(): ContatoId = ContatoId(newId())

        fun toUUID(uuid: String): ContatoId = ContatoId(parse(uuid))
    }
}
