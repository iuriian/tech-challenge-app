package br.com.fiap.oficina.peca.domain

import br.com.fiap.oficina.shared.domain.AggregateId
import java.util.UUID

data class PecaId(override val value: UUID) : AggregateId() {
    companion object {
        fun generate(): PecaId = PecaId(newId())

        fun toUUID(uuid: String): PecaId = PecaId(parse(uuid))
    }
}
