package br.com.fiap.oficina.veiculo.infrastructure.acl

import br.com.fiap.oficina.cliente.application.port.ClienteQueryPort
import br.com.fiap.oficina.veiculo.domain.ProprietarioRef
import br.com.fiap.oficina.veiculo.domain.port.ConsultarProprietario
import br.com.fiap.oficina.veiculo.domain.port.DadosProprietario

internal class ConsultarProprietarioAdapter(val queryPort: ClienteQueryPort, val mapper: ProprietarioMapper) :
    ConsultarProprietario {
    override fun buscarPorId(ref: ProprietarioRef): DadosProprietario? =
        queryPort.buscarPorId(ref.value)?.let(mapper::toProviderResponse)
}
