package br.com.fiap.oficina.funcionario.domain

import java.util.UUID

// TODO: mover para o módulo compartilhado
sealed class AggregateId {
    abstract val value: UUID

    companion object {
        fun newId(): UUID = UUID.randomUUID()
    }

    override fun toString(): String = value.toString()
}

data class FuncionarioId(
    override val value: UUID,
) : AggregateId() {
    companion object {
        fun generate(): FuncionarioId = FuncionarioId(newId())

        fun from(uuid: UUID): FuncionarioId = FuncionarioId(uuid)
    }
}
