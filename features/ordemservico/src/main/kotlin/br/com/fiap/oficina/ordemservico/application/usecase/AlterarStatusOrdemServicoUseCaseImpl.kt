package br.com.fiap.oficina.ordemservico.application.usecase

import br.com.fiap.oficina.ordemservico.application.dto.AlterarStatusRequest
import br.com.fiap.oficina.ordemservico.application.dto.OrdemServicoResponse
import br.com.fiap.oficina.ordemservico.application.mapper.OrdemServicoMapper
import br.com.fiap.oficina.ordemservico.domain.OrdemServicoId
import br.com.fiap.oficina.ordemservico.domain.OrdemServicoRepository
import br.com.fiap.oficina.shared.domain.exception.BusinessRuleException
import br.com.fiap.oficina.shared.domain.exception.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
internal class AlterarStatusOrdemServicoUseCaseImpl(
    private val repository: OrdemServicoRepository,
    private val mapper: OrdemServicoMapper,
) : AlterarStatusOrdemServicoUseCase {
    @Transactional
    override fun executar(id: String, request: AlterarStatusRequest): OrdemServicoResponse {
        val ordemServico =
            repository.buscarPorId(OrdemServicoId.toUUID(id))
                ?: throw EntityNotFoundException("Ordem de serviço não encontrada, id: $id")

        val permitidas = ordemServico.status.transicoesPermitidas()

        if (request.status !in permitidas) {
            throw BusinessRuleException(
                "Transição inválida de '${ordemServico.status}' para '${request.status}'. " +
                    "Transições permitidas a partir de '${ordemServico.status}': $permitidas",
            )
        }

        return mapper.toResponse(repository.salvar(ordemServico.alterarStatus(request.status)))
    }
}
