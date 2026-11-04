package br.com.fiap.oficina.ordemservico.infrastructure.acl.cliente

import br.com.fiap.oficina.cliente.application.port.ClienteQueryPort
import br.com.fiap.oficina.ordemservico.domain.ClienteRef
import br.com.fiap.oficina.ordemservico.domain.port.cliente.ConsultarCliente
import br.com.fiap.oficina.ordemservico.domain.port.cliente.DadosCliente

internal class ConsultarClienteAdapter(private val queryPort: ClienteQueryPort, private val mapper: ClienteMapper) :
    ConsultarCliente {
    override fun buscarPorId(ref: ClienteRef): DadosCliente? =
        queryPort.buscarPorId(ref.value)?.let(mapper::toProviderResponse)
}
