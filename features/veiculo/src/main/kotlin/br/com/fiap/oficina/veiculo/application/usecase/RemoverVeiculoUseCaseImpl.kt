package br.com.fiap.oficina.veiculo.application.usecase

import br.com.fiap.oficina.shared.domain.exception.EntityNotFoundException
import br.com.fiap.oficina.veiculo.domain.VeiculoId
import br.com.fiap.oficina.veiculo.domain.VeiculoRepository
import org.springframework.stereotype.Service

@Service
internal class RemoverVeiculoUseCaseImpl(private val repository: VeiculoRepository) : RemoverVeiculoUseCase {
    override fun executar(id: String) {
        val veiculo =
            repository.buscarPorId(VeiculoId.toUUID(id))
                ?: throw EntityNotFoundException("Veículo não encontrado para o id: $id")

        repository.deletar(veiculo.id)
    }
}
