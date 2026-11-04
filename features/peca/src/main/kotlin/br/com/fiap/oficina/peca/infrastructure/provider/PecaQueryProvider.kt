package br.com.fiap.oficina.peca.infrastructure.provider

import br.com.fiap.oficina.peca.application.port.PecaQueryDto
import br.com.fiap.oficina.peca.application.port.PecaQueryPort
import br.com.fiap.oficina.peca.domain.PecaId
import br.com.fiap.oficina.peca.domain.PecaRepository
import java.util.UUID

internal class PecaQueryProvider(private val repository: PecaRepository, private val mapper: PecaQueryMapper) :
    PecaQueryPort {
    override fun buscarPorId(id: UUID): PecaQueryDto? =
        repository.buscarPorId(PecaId(id))?.let(mapper::toProviderResponse)
}
