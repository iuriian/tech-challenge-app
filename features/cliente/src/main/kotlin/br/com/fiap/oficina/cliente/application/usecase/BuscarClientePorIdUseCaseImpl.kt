package br.com.fiap.oficina.cliente.application.usecase

import br.com.fiap.oficina.cliente.application.dto.ClienteResponse
import br.com.fiap.oficina.cliente.application.mapper.ClienteMapper
import br.com.fiap.oficina.cliente.domain.ClienteId
import br.com.fiap.oficina.cliente.domain.ClienteRepository
import br.com.fiap.oficina.shared.domain.exception.EntityNotFoundException
import org.springframework.stereotype.Service

@Service
internal class BuscarClientePorIdUseCaseImpl(
    private val repository: ClienteRepository,
    private val mapper: ClienteMapper,
) : BuscarClientePorIdUseCase {
    override fun executar(id: String): ClienteResponse {
        val cliente =
            repository.buscarPorId(ClienteId.toUUID(id))
                ?: throw EntityNotFoundException("Cliente não encontrado, id: $id")

        return mapper.toResponse(cliente)
    }
}
