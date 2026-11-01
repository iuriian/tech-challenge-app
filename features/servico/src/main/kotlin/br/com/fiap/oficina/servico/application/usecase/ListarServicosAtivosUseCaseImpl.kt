package br.com.fiap.oficina.servico.application.usecase

import br.com.fiap.oficina.servico.application.dto.ServicoResponse
import br.com.fiap.oficina.servico.application.mapper.ServicoMapper
import br.com.fiap.oficina.servico.domain.ServicoRepository
import org.springframework.stereotype.Service

@Service
internal class ListarServicosAtivosUseCaseImpl(
    private val repository: ServicoRepository,
    private val mapper: ServicoMapper,
) : ListarServicosAtivosUseCase {
    override fun executar(): List<ServicoResponse> = repository.listarAtivos().map(mapper::toResponse)
}
