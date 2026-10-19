package br.com.fiap.oficina.veiculo.application.usecase

import br.com.fiap.oficina.shared.domain.exception.BusinessRuleException
import br.com.fiap.oficina.veiculo.application.dto.VeiculoRequest
import br.com.fiap.oficina.veiculo.application.dto.VeiculoResponse
import br.com.fiap.oficina.veiculo.application.mapper.VeiculoMapper
import br.com.fiap.oficina.veiculo.domain.VeiculoRepository
import br.com.fiap.oficina.veiculo.domain.port.ConsultarProprietario
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
internal class CriarVeiculoUseCaseImpl(
    private val repository: VeiculoRepository,
    private val consultarProprietario: ConsultarProprietario,
    private val mapper: VeiculoMapper,
) : CriarVeiculoUseCase {
    @Transactional
    override fun executar(request: VeiculoRequest): VeiculoResponse {
        val veiculo = mapper.toDomain(request)

        if (!repository.existeVeiculo(veiculo.placa)) {
            throw BusinessRuleException("Já existe veículo cadastrado com a placa ${veiculo.placa}")
        }

        val proprietario = consultarProprietario.buscarPorId(veiculo.proprietarioRef)

        return mapper.toResponse(repository.salvar(veiculo), proprietario)
    }
}
