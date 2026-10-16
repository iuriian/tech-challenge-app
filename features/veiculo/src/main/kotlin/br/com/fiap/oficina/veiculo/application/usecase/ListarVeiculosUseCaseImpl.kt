package br.com.fiap.oficina.veiculo.application.usecase

import br.com.fiap.oficina.veiculo.application.dto.VeiculoResponse
import br.com.fiap.oficina.veiculo.application.mapper.VeiculoMapper
import br.com.fiap.oficina.veiculo.domain.VeiculoRepository
import org.springframework.stereotype.Service

@Service
internal class ListarVeiculosUseCaseImpl(
    private val repository: VeiculoRepository,
    private val mapper: VeiculoMapper,
) : ListarVeiculosUseCase {
    override fun executar(): List<VeiculoResponse> {
        val veiculos = repository.listarTodos()
        return veiculos.map(mapper::toResponse)
    }
}
