package br.com.fiap.oficina.cliente.application.usecase

import br.com.fiap.oficina.cliente.application.dto.ClienteRequest
import br.com.fiap.oficina.cliente.application.dto.ClienteResponse
import br.com.fiap.oficina.cliente.application.mapper.ClienteMapper
import br.com.fiap.oficina.cliente.domain.ClienteRepository
import br.com.fiap.oficina.shared.domain.exception.BusinessRuleException
import org.springframework.stereotype.Service

@Service
internal class CriarClienteUseCaseImpl(private val repository: ClienteRepository, private val mapper: ClienteMapper) :
    CriarClienteUseCase {
    override fun executar(request: ClienteRequest): ClienteResponse {
        val cliente = mapper.toDomain(request)

        if (repository.buscarPorDocumento(cliente.documento) != null) {
            throw BusinessRuleException("Cliente já cadastrado")
        }

        return mapper.toResponse(repository.salvar(cliente))
    }
}
