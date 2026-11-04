package br.com.fiap.oficina.veiculo.infrastructure.provider

import br.com.fiap.oficina.shared.domain.mapper.QueryMapper
import br.com.fiap.oficina.veiculo.application.port.VeiculoQueryDto
import br.com.fiap.oficina.veiculo.domain.Veiculo

internal class VeiculoQueryMapper : QueryMapper<Veiculo, VeiculoQueryDto> {
    override fun toProviderResponse(domain: Veiculo): VeiculoQueryDto = VeiculoQueryDto(
        id = domain.id.toString(),
        placa = domain.placa.value,
        proprietarioId = domain.proprietarioRef.toString(),
    )
}
