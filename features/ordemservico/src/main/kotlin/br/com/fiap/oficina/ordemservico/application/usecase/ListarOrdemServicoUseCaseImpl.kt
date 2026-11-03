package br.com.fiap.oficina.ordemservico.application.usecase

import br.com.fiap.oficina.ordemservico.application.dto.OrdemServicoResponse
import br.com.fiap.oficina.ordemservico.application.mapper.OrdemServicoMapper
import br.com.fiap.oficina.ordemservico.domain.OrdemServicoRepository
import org.springframework.stereotype.Service

@Service
internal class ListarOrdemServicoUseCaseImpl(
    private val repository: OrdemServicoRepository,
    private val mapper: OrdemServicoMapper,
) : ListarOrdemServicoUseCase {
    override fun executar(): List<OrdemServicoResponse> = repository
        .listarTodos()
        .map(mapper::toResponse)
}
