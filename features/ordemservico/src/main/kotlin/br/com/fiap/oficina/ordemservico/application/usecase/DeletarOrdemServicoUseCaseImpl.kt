package br.com.fiap.oficina.ordemservico.application.usecase

import br.com.fiap.oficina.ordemservico.domain.OrdemServicoId
import br.com.fiap.oficina.ordemservico.domain.OrdemServicoRepository
import br.com.fiap.oficina.shared.domain.exception.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
internal class DeletarOrdemServicoUseCaseImpl(private val repository: OrdemServicoRepository) :
    DeletarOrdemServicoUseCase {
    @Transactional
    override fun executar(id: String) {
        val removida = repository.deletar(OrdemServicoId.toUUID(id))

        if (!removida) {
            throw EntityNotFoundException("Ordem de serviço não encontrada com o ID: $id")
        }
    }
}
