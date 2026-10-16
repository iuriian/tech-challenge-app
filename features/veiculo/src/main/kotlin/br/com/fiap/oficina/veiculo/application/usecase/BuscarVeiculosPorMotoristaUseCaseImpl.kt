package br.com.fiap.oficina.veiculo.application.usecase

import br.com.fiap.oficina.veiculo.application.dto.VeiculoResponse
import br.com.fiap.oficina.veiculo.application.mapper.VeiculoMapper
import br.com.fiap.oficina.veiculo.domain.ProprietarioId
import br.com.fiap.oficina.veiculo.domain.VeiculoRepository
import org.springframework.stereotype.Service

@Service
internal class BuscarVeiculosPorMotoristaUseCaseImpl(
    private val repository: VeiculoRepository,
    private val mapper: VeiculoMapper,
) : BuscarVeiculosPorMotoristaUseCase {
    override fun executar(id: String): List<VeiculoResponse> {
        val veiculos = repository.buscarPorMotorista(ProprietarioId.toUUID(id))
        return veiculos.map(mapper::toResponse)
    }
}
