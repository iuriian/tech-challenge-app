package br.com.fiap.oficina.ordemservico.application.usecase

import br.com.fiap.oficina.ordemservico.application.dto.OrdemServicoResponse
import br.com.fiap.oficina.ordemservico.application.mapper.OrdemServicoMapper
import br.com.fiap.oficina.ordemservico.domain.OrdemServicoId
import br.com.fiap.oficina.ordemservico.domain.OrdemServicoRepository
import br.com.fiap.oficina.shared.domain.exception.BusinessRuleException
import br.com.fiap.oficina.shared.domain.exception.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
internal class AvancarStatusOrdemServicoUseCaseImpl(
    private val repository: OrdemServicoRepository,
    private val mapper: OrdemServicoMapper,
) : AvancarStatusOrdemServicoUseCase {
    @Transactional
    override fun executar(id: String): OrdemServicoResponse {
        val ordemServico =
            repository.buscarPorId(OrdemServicoId.toUUID(id))
                ?: throw EntityNotFoundException("Ordem de serviço não encontrada, id: $id")

        ordemServico.status.proximoStatus()
            ?: throw BusinessRuleException(
                "Ordem de serviço no status '${ordemServico.status}' é um estado final e não pode ser avançada",
            )

        return mapper.toResponse(repository.salvar(ordemServico.avancarStatus()))
    }
}
