package br.com.fiap.oficina.cliente.application.usecase

import br.com.fiap.oficina.cliente.application.dto.ClienteResponse
import br.com.fiap.oficina.cliente.application.mapper.ClienteMapper
import br.com.fiap.oficina.cliente.domain.ClienteRepository
import org.springframework.stereotype.Service

@Service
internal class ListarClientesUseCaseImpl(
    private val repository: ClienteRepository,
    private val mapper: ClienteMapper,
) : ListarClientesUseCase {
    override fun executar(): List<ClienteResponse> {
        val clientes = repository.listarTodos()

        return clientes.map(mapper::toResponse)
    }
}
