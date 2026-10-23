package br.com.fiap.oficina.shared.domain

import java.util.UUID

abstract class AggregateId {
    abstract val value: UUID

    final override fun toString(): String = value.toString()

    companion object {
        fun newId(): UUID = UUID.randomUUID()

        fun parse(value: String): UUID = UUID.fromString(value)
    }
}
