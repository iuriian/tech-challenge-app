package br.com.fiap.oficina.servico.domain

import br.com.fiap.oficina.shared.domain.AggregateId
import java.util.UUID

data class ServicoId(override val value: UUID) : AggregateId() {
    companion object {
        fun generate(): ServicoId = ServicoId(newId())

        fun toUUID(uuid: String): ServicoId = ServicoId(parse(uuid))
    }
}
