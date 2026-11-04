package br.com.fiap.oficina.funcionario.infrastructure.provider

import br.com.fiap.oficina.funcionario.application.port.FuncionarioQueryDto
import br.com.fiap.oficina.funcionario.application.port.FuncionarioQueryPort
import br.com.fiap.oficina.funcionario.domain.FuncionarioId
import br.com.fiap.oficina.funcionario.domain.FuncionarioRepository
import java.util.UUID

internal class FuncionarioQueryProvider(
    private val repository: FuncionarioRepository,
    private val mapper: FuncionarioQueryMapper,
) : FuncionarioQueryPort {
    override fun buscarPorId(id: UUID): FuncionarioQueryDto? =
        repository.buscarPorId(FuncionarioId(id))?.let(mapper::toProviderResponse)
}
