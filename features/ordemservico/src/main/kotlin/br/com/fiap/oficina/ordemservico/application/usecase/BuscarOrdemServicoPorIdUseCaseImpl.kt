package br.com.fiap.oficina.ordemservico.application.usecase

import br.com.fiap.oficina.ordemservico.application.dto.OrdemServicoResponse
import br.com.fiap.oficina.ordemservico.application.mapper.OrdemServicoMapper
import br.com.fiap.oficina.ordemservico.domain.OrdemServicoId
import br.com.fiap.oficina.ordemservico.domain.OrdemServicoRepository
import br.com.fiap.oficina.shared.domain.exception.EntityNotFoundException
import org.springframework.stereotype.Service

@Service
internal class BuscarOrdemServicoPorIdUseCaseImpl(
    private val repository: OrdemServicoRepository,
    private val mapper: OrdemServicoMapper,
) : BuscarOrdemServicoPorIdUseCase {
    override fun executar(id: String): OrdemServicoResponse {
        val ordemServico =
            repository.buscarPorId(OrdemServicoId.toUUID(id))
                ?: throw EntityNotFoundException("Ordem de serviço não encontrada, id: $id")

        return mapper.toResponse(ordemServico)
    }
}
