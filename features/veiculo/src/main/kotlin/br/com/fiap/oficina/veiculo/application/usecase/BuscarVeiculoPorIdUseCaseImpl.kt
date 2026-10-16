package br.com.fiap.oficina.veiculo.application.usecase

import br.com.fiap.oficina.shared.domain.exception.EntityNotFoundException
import br.com.fiap.oficina.veiculo.application.dto.VeiculoResponse
import br.com.fiap.oficina.veiculo.application.mapper.VeiculoMapper
import br.com.fiap.oficina.veiculo.domain.VeiculoId
import br.com.fiap.oficina.veiculo.domain.VeiculoRepository
import org.springframework.stereotype.Service

@Service
internal class BuscarVeiculoPorIdUseCaseImpl(
    private val repository: VeiculoRepository,
    private val mapper: VeiculoMapper,
) : BuscarVeiculoPorIdUseCase {
    override fun executar(id: String): VeiculoResponse {
        val veiculo =
            repository.buscarPorId(VeiculoId.toUUID(id))
                ?: throw EntityNotFoundException("Veículo com ID $id não encontrado")

        return mapper.toResponse(veiculo)
    }
}
