package br.com.fiap.oficina.veiculo.application.usecase

import br.com.fiap.oficina.shared.domain.exception.BusinessRuleException
import br.com.fiap.oficina.shared.domain.exception.EntityNotFoundException
import br.com.fiap.oficina.shared.domain.valueobject.Placa
import br.com.fiap.oficina.veiculo.application.dto.VeiculoRequest
import br.com.fiap.oficina.veiculo.application.dto.VeiculoResponse
import br.com.fiap.oficina.veiculo.application.mapper.VeiculoMapper
import br.com.fiap.oficina.veiculo.domain.Veiculo
import br.com.fiap.oficina.veiculo.domain.VeiculoId
import br.com.fiap.oficina.veiculo.domain.VeiculoRepository
import br.com.fiap.oficina.veiculo.domain.port.ConsultarProprietario
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
internal class AtualizarVeiculoUseCaseImpl(
    private val repository: VeiculoRepository,
    private val mapper: VeiculoMapper,
    private val consultarProprietario: ConsultarProprietario,
) : AtualizarVeiculoUseCase {
    @Transactional
    override fun executar(id: String, request: VeiculoRequest): VeiculoResponse {
        val veiculoRequest = mapper.toDomain(request)
        val veiculo =
            repository.buscarPorId(VeiculoId.toUUID(id))
                ?: throw EntityNotFoundException("Veículo não encontrado com o id: $id")

        val proprietario =
            consultarProprietario.buscarPorId(veiculoRequest.proprietarioRef)
                ?: throw BusinessRuleException(
                    "Proprietário não encontrado com o id: ${veiculoRequest.proprietarioRef}",
                )

        validarPlaca(veiculoRequest.placa, veiculo)

        val atualizado =
            veiculo.atualizar(
                marca = veiculoRequest.marca,
                nome = veiculoRequest.nome,
                modelo = veiculoRequest.modelo,
                ano = veiculoRequest.ano,
                placa = veiculoRequest.placa,
                proprietarioRef = veiculoRequest.proprietarioRef,
            )

        return mapper.toResponse(
            repository.salvar(atualizado),
            proprietario,
        )
    }

    private fun validarPlaca(placa: Placa, veiculo: Veiculo) {
        if (placa != veiculo.placa) {
            val veiculoCadastrado = repository.buscarPorPlaca(placa)

            if (veiculoCadastrado != null) {
                throw BusinessRuleException("Já existe um veículo cadastrado com a placa: $placa")
            }
        }
    }
}
