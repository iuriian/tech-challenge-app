package br.com.fiap.oficina.ordemservico.application.usecase

import br.com.fiap.oficina.ordemservico.application.dto.OrdemServicoRequest
import br.com.fiap.oficina.ordemservico.application.dto.OrdemServicoResponse
import br.com.fiap.oficina.ordemservico.application.mapper.OrdemServicoMapper
import br.com.fiap.oficina.ordemservico.application.resolver.OrcamentoResolver
import br.com.fiap.oficina.ordemservico.application.resolver.ReferenciaResolver
import br.com.fiap.oficina.ordemservico.domain.OrdemServicoId
import br.com.fiap.oficina.ordemservico.domain.OrdemServicoRepository
import br.com.fiap.oficina.shared.domain.exception.BusinessRuleException
import br.com.fiap.oficina.shared.domain.exception.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
internal class AtualizarOrdemServicoUseCaseImpl(
    private val repository: OrdemServicoRepository,
    private val referenciaResolver: ReferenciaResolver,
    private val orcamentoResolver: OrcamentoResolver,
    private val mapper: OrdemServicoMapper,
) : AtualizarOrdemServicoUseCase {
    @Transactional
    override fun executar(id: String, request: OrdemServicoRequest): OrdemServicoResponse {
        val ordemServico =
            repository.buscarPorId(OrdemServicoId.toUUID(id))
                ?: throw EntityNotFoundException("Ordem de serviço não encontrada, id: $id")

        if (!ordemServico.podeSerAtualizada()) {
            throw BusinessRuleException(
                "Ordem de serviço no status '${ordemServico.status}' não pode ser atualizada",
            )
        }

        val referencias = referenciaResolver.resolver(request)

        val atualizada =
            ordemServico.atualizar(
                descricao = request.descricao,
                funcionarioRef = referencias.funcionarioRef,
                clienteRef = referencias.clienteRef,
                veiculoRef = referencias.veiculoRef,
                orcamento = orcamentoResolver.resolver(request.itens),
            )

        return mapper.toResponse(repository.salvar(atualizada))
    }
}
