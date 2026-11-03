package br.com.fiap.oficina.ordemservico.application.usecase

import br.com.fiap.oficina.ordemservico.application.dto.OrcamentoResponse
import br.com.fiap.oficina.ordemservico.application.mapper.OrcamentoMapper
import br.com.fiap.oficina.ordemservico.domain.OrdemServicoId
import br.com.fiap.oficina.ordemservico.domain.OrdemServicoRepository
import br.com.fiap.oficina.shared.domain.exception.EntityNotFoundException
import org.springframework.stereotype.Service

@Service
internal class ObterOrcamentoUseCaseImpl(
    private val repository: OrdemServicoRepository,
    private val mapper: OrcamentoMapper,
) : ObterOrcamentoUseCase {
    override fun executar(id: String): OrcamentoResponse {
        val ordemServico =
            repository.buscarPorId(OrdemServicoId.toUUID(id))
                ?: throw EntityNotFoundException("Ordem de serviço não encontrada, id: $id")

        return mapper.toResponse(ordemServico.orcamento)
    }
}
