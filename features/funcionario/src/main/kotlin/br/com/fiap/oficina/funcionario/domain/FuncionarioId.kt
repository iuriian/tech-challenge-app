package br.com.fiap.oficina.funcionario.domain

import java.util.UUID

sealed class AggregateId {
    abstract val value: UUID

    companion object {
        fun newId(): UUID = UUID.randomUUID()
    }

    final override fun toString(): String = value.toString()
}

data class FuncionarioId(override val value: UUID) : AggregateId() {
    companion object {
        fun generate(): FuncionarioId = FuncionarioId(newId())

        fun toUUID(uuid: String): FuncionarioId = FuncionarioId(UUID.fromString(uuid))
    }
}
