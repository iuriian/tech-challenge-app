package br.com.fiap.oficina.cliente.infrastructure.provider

import br.com.fiap.oficina.cliente.application.port.ClienteQueryDto
import br.com.fiap.oficina.cliente.domain.Cliente
import br.com.fiap.oficina.shared.domain.mapper.QueryMapper

internal class ClienteQueryMapper : QueryMapper<Cliente, ClienteQueryDto> {
    override fun toProviderResponse(domain: Cliente): ClienteQueryDto =
        ClienteQueryDto(
            id = domain.id.toString(),
            nome = domain.nome,
            numeroDocumento = domain.documento.formatado(),
        )
}
