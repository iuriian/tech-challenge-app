package br.com.fiap.oficina.cliente.application.usecase

import br.com.fiap.oficina.cliente.application.dto.ClienteResponse
import br.com.fiap.oficina.cliente.application.mapper.ClienteMapper
import br.com.fiap.oficina.cliente.domain.ClienteRepository
import br.com.fiap.oficina.shared.domain.exception.EntityNotFoundException
import org.springframework.stereotype.Service

@Service
internal class BuscarClientePorNomeUseCaseImpl(
    private val repository: ClienteRepository,
    private val mapper: ClienteMapper,
) : BuscarClientePorNomeUseCase {
    override fun executar(nome: String): ClienteResponse {
        val cliente =
            repository.buscarPorNome(nome)
                ?: throw EntityNotFoundException("Cliente não encontrado com o nome: $nome")

        return mapper.toResponse(cliente)
    }
}
