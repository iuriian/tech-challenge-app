package br.com.fiap.oficina.ordemservico.application.usecase

import br.com.fiap.oficina.ordemservico.application.dto.OrdemServicoResponse
import br.com.fiap.oficina.ordemservico.application.mapper.OrdemServicoMapper
import br.com.fiap.oficina.ordemservico.domain.ClienteRef
import br.com.fiap.oficina.ordemservico.domain.OrdemServicoRepository
import org.springframework.stereotype.Service

@Service
internal class BuscarOrdemServicoPorClienteUseCaseImpl(
    private val repository: OrdemServicoRepository,
    private val mapper: OrdemServicoMapper,
) : BuscarOrdemServicoPorClienteUseCase {
    override fun executar(id: String): List<OrdemServicoResponse> =
        repository.buscarPorCliente(ClienteRef.toUUID(id)).map(mapper::toResponse)
}
