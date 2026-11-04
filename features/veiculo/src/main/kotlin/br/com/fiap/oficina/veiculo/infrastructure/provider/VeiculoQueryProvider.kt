package br.com.fiap.oficina.veiculo.infrastructure.provider

import br.com.fiap.oficina.veiculo.application.port.VeiculoQueryDto
import br.com.fiap.oficina.veiculo.application.port.VeiculoQueryPort
import br.com.fiap.oficina.veiculo.domain.VeiculoId
import br.com.fiap.oficina.veiculo.domain.VeiculoRepository
import java.util.UUID

internal class VeiculoQueryProvider(
    private val repository: VeiculoRepository,
    private val mapper: VeiculoQueryMapper,
) : VeiculoQueryPort {
    override fun buscarPorId(id: UUID): VeiculoQueryDto? =
        repository.buscarPorId(VeiculoId(id))?.let(mapper::toProviderResponse)
}
