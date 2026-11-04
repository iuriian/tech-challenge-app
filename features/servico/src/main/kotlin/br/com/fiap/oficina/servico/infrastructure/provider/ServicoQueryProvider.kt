package br.com.fiap.oficina.servico.infrastructure.provider

import br.com.fiap.oficina.servico.application.port.ServicoQueryDto
import br.com.fiap.oficina.servico.application.port.ServicoQueryPort
import br.com.fiap.oficina.servico.domain.ServicoId
import br.com.fiap.oficina.servico.domain.ServicoRepository
import java.util.UUID

internal class ServicoQueryProvider(private val repository: ServicoRepository, private val mapper: ServicoQueryMapper) :
    ServicoQueryPort {
    override fun buscarPorId(id: UUID): ServicoQueryDto? =
        repository.buscarPorId(ServicoId(id))?.let(mapper::toProviderResponse)
}
