package br.com.fiap.oficina.peca.domain

import java.util.UUID

sealed class AggregateId {
    abstract val value: UUID

    companion object {
        fun newId(): UUID = UUID.randomUUID()
    }

    final override fun toString(): String = value.toString()
}

data class PecaId(override val value: UUID) : AggregateId() {
    companion object {
        fun generate(): PecaId = PecaId(newId())

        fun toUUID(uuid: String): PecaId = PecaId(UUID.fromString(uuid))
    }
}
