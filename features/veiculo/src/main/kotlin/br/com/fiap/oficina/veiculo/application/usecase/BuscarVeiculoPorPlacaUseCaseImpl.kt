package br.com.fiap.oficina.veiculo.application.usecase

import br.com.fiap.oficina.shared.domain.exception.EntityNotFoundException
import br.com.fiap.oficina.veiculo.application.dto.VeiculoResponse
import br.com.fiap.oficina.veiculo.application.mapper.VeiculoMapper
import br.com.fiap.oficina.veiculo.domain.VeiculoRepository
import org.springframework.stereotype.Service

@Service
internal class BuscarVeiculoPorPlacaUseCaseImpl(
    private val repository: VeiculoRepository,
    private val mapper: VeiculoMapper,
) : BuscarVeiculoPorPlacaUseCase {
    override fun executar(placa: String): VeiculoResponse {
        val veiculo =
            repository.buscarPorPlaca(placa)
                ?: throw EntityNotFoundException("Veículo com placa $placa não encontrado")

        return mapper.toResponse(veiculo)
    }
}
