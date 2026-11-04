package br.com.fiap.oficina.ordemservico.infrastructure.acl.veiculo

import br.com.fiap.oficina.ordemservico.domain.VeiculoRef
import br.com.fiap.oficina.ordemservico.domain.port.veiculo.DadosVeiculo
import br.com.fiap.oficina.shared.domain.mapper.QueryMapper
import br.com.fiap.oficina.veiculo.application.port.VeiculoQueryDto

internal class VeiculoMapper : QueryMapper<VeiculoQueryDto, DadosVeiculo> {
    override fun toProviderResponse(dto: VeiculoQueryDto): DadosVeiculo = DadosVeiculo(
        id = VeiculoRef.toUUID(dto.id),
        placa = dto.placa,
    )
}
