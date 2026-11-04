package br.com.fiap.oficina.ordemservico.infrastructure.acl.veiculo

import br.com.fiap.oficina.ordemservico.domain.VeiculoRef
import br.com.fiap.oficina.ordemservico.domain.port.veiculo.ConsultarVeiculo
import br.com.fiap.oficina.ordemservico.domain.port.veiculo.DadosVeiculo
import br.com.fiap.oficina.veiculo.application.port.VeiculoQueryPort

internal class ConsultarVeiculoAdapter(private val queryPort: VeiculoQueryPort, private val mapper: VeiculoMapper) :
    ConsultarVeiculo {
    override fun buscarPorId(ref: VeiculoRef): DadosVeiculo? =
        queryPort.buscarPorId(ref.value)?.let(mapper::toProviderResponse)
}
