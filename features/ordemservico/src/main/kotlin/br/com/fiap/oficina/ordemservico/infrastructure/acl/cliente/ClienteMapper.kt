package br.com.fiap.oficina.ordemservico.infrastructure.acl.cliente

import br.com.fiap.oficina.cliente.application.port.ClienteQueryDto
import br.com.fiap.oficina.ordemservico.domain.ClienteRef
import br.com.fiap.oficina.ordemservico.domain.port.cliente.DadosCliente
import br.com.fiap.oficina.shared.domain.mapper.QueryMapper

internal class ClienteMapper : QueryMapper<ClienteQueryDto, DadosCliente> {
    override fun toProviderResponse(dto: ClienteQueryDto): DadosCliente = DadosCliente(
        id = ClienteRef.toUUID(dto.id),
    )
}
