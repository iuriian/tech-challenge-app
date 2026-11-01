package br.com.fiap.oficina.cliente.application.usecase

import br.com.fiap.oficina.cliente.domain.ClienteId
import br.com.fiap.oficina.cliente.domain.ClienteRepository
import br.com.fiap.oficina.shared.domain.exception.EntityNotFoundException
import org.springframework.stereotype.Service

@Service
internal class RemoverClienteUseCaseImpl(private val repository: ClienteRepository) : RemoverClienteUseCase {
    override fun executar(id: String) {
        val cliente =
            repository.buscarPorId(ClienteId.toUUID(id))
                ?: throw EntityNotFoundException("Cliente não encontrado com o ID: $id")

        repository.deletar(cliente.id)
    }
}
