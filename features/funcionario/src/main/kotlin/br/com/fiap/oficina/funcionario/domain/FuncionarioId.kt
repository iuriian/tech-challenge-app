package br.com.fiap.oficina.funcionario.domain

import br.com.fiap.oficina.shared.domain.AggregateId
import java.util.UUID

data class FuncionarioId(override val value: UUID) : AggregateId() {
    companion object {
        fun generate(): FuncionarioId = FuncionarioId(newId())

        fun toUUID(uuid: String): FuncionarioId = FuncionarioId(parse(uuid))
    }
}
