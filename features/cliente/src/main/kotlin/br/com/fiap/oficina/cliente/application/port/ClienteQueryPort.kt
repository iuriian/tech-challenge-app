package br.com.fiap.oficina.cliente.application.port

import java.util.UUID

interface ClienteQueryPort {
    fun buscarPorId(id: UUID): ClienteQueryDto
}
