package br.com.fiap.oficina.cliente.application.usecase

import br.com.fiap.oficina.cliente.application.dto.ClienteResponse
import br.com.fiap.oficina.cliente.application.mapper.ClienteMapper
import br.com.fiap.oficina.cliente.domain.ClienteRepository
import br.com.fiap.oficina.shared.domain.exception.EntityNotFoundException
import br.com.fiap.oficina.shared.domain.valueobject.Document
import org.springframework.stereotype.Service

@Service
internal class BuscarClientePorDocumentoUseCaseImpl(
    private val repository: ClienteRepository,
    private val mapper: ClienteMapper,
) : BuscarClientePorDocumentoUseCase {
    override fun executar(numeroDocumento: String): ClienteResponse {
        val cliente =
            repository.buscarPorDocumento(Document.from(numeroDocumento))
                ?: throw EntityNotFoundException("Cliente não encontrado, documento: $numeroDocumento")

        return mapper.toResponse(cliente)
    }
}
