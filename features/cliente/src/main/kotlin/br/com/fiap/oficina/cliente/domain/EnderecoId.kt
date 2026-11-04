package br.com.fiap.oficina.cliente.domain

import br.com.fiap.oficina.shared.domain.AggregateId
import java.util.UUID

data class EnderecoId(override val value: UUID) : AggregateId() {
    companion object {
        fun generate(): EnderecoId = EnderecoId(newId())

        fun toUUID(uuid: String): EnderecoId = EnderecoId(parse(uuid))
    }
}
