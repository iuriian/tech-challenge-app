package br.com.fiap.oficina.veiculo.application.usecase

import br.com.fiap.oficina.veiculo.application.dto.VeiculoResponse
import br.com.fiap.oficina.veiculo.application.mapper.VeiculoMapper
import br.com.fiap.oficina.veiculo.domain.Veiculo
import br.com.fiap.oficina.veiculo.domain.VeiculoRepository
import br.com.fiap.oficina.veiculo.domain.port.ConsultarProprietario
import org.springframework.stereotype.Service

@Service
internal class ListarVeiculosUseCaseImpl(
    private val repository: VeiculoRepository,
    private val mapper: VeiculoMapper,
    private val consultarProprietario: ConsultarProprietario,
) : ListarVeiculosUseCase {
    override fun executar(): List<VeiculoResponse> {
        val veiculos = repository.listarTodos()
        val proprietarios =
            veiculos
                .map(Veiculo::proprietarioRef)
                .distinct()
                .associateWith(consultarProprietario::buscarPorId)

        return veiculos.map { mapper.toResponse(it, proprietarios[it.proprietarioRef]) }
    }
}
