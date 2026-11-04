package br.com.fiap.oficina.veiculo.application.usecase

import br.com.fiap.oficina.veiculo.application.dto.VeiculoResponse
import br.com.fiap.oficina.veiculo.application.mapper.VeiculoMapper
import br.com.fiap.oficina.veiculo.domain.ProprietarioRef
import br.com.fiap.oficina.veiculo.domain.VeiculoRepository
import br.com.fiap.oficina.veiculo.domain.port.ConsultarProprietario
import org.springframework.stereotype.Service

@Service
internal class BuscarVeiculosPorProprietarioUseCaseImpl(
    private val repository: VeiculoRepository,
    private val mapper: VeiculoMapper,
    private val consultarProprietario: ConsultarProprietario,
) : BuscarVeiculosPorProprietarioUseCase {
    override fun executar(id: String): List<VeiculoResponse> {
        val ref = ProprietarioRef.toUUID(id)
        val proprietario = consultarProprietario.buscarPorId(ref)

        return repository
            .buscarPorProprietario(ref)
            .map { mapper.toResponse(it, proprietario) }
    }
}
