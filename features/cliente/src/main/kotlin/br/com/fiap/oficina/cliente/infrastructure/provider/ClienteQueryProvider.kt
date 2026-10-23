package br.com.fiap.oficina.cliente.infrastructure.provider

import br.com.fiap.oficina.cliente.application.port.ClienteQueryDto
import br.com.fiap.oficina.cliente.application.port.ClienteQueryPort
import br.com.fiap.oficina.cliente.domain.ClienteId
import br.com.fiap.oficina.cliente.domain.ClienteRepository
import java.util.UUID

internal class ClienteQueryProvider(
    private val repository: ClienteRepository,
    private val mapper: ClienteQueryMapper,
) : ClienteQueryPort {
    override fun buscarPorId(id: UUID): ClienteQueryDto? =
        repository.buscarPorId(ClienteId(id))?.let(mapper::toProviderResponse)
}
