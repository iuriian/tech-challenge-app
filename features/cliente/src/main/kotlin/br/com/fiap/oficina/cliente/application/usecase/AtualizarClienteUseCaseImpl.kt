package br.com.fiap.oficina.cliente.application.usecase

import br.com.fiap.oficina.cliente.application.dto.ClienteRequest
import br.com.fiap.oficina.cliente.application.dto.ClienteResponse
import br.com.fiap.oficina.cliente.application.mapper.ClienteMapper
import br.com.fiap.oficina.cliente.domain.Cliente
import br.com.fiap.oficina.cliente.domain.ClienteId
import br.com.fiap.oficina.cliente.domain.ClienteRepository
import br.com.fiap.oficina.shared.domain.exception.BusinessRuleException
import br.com.fiap.oficina.shared.domain.exception.EntityNotFoundException
import br.com.fiap.oficina.shared.domain.valueobject.Document
import org.springframework.stereotype.Service

@Service
internal class AtualizarClienteUseCaseImpl(
    private val repository: ClienteRepository,
    private val mapper: ClienteMapper,
) : AtualizarClienteUseCase {
    override fun executar(id: String, request: ClienteRequest): ClienteResponse {
        require(id.isNotBlank()) { "Id é obrigatório!" }

        val cliente =
            repository.buscarPorId(ClienteId.toUUID(id))
                ?: throw EntityNotFoundException("Cliente não encontrado!")

        val clienteRequest = mapper.toDomain(request)

        validarDocumento(clienteRequest.documento, cliente)

        val atualizado =
            cliente.atualizar(
                nome = clienteRequest.nome,
                documento = clienteRequest.documento,
                email = clienteRequest.email,
                endereco = clienteRequest.endereco,
                contatos = clienteRequest.contatos,
            )

        return mapper.toResponse(repository.salvar(atualizado))
    }

    private fun validarDocumento(documento: Document, cliente: Cliente) {
        if (documento.valor == cliente.documento.valor) return

        val clienteCadastrado = repository.buscarPorDocumento(documento)

        if (clienteCadastrado != null && clienteCadastrado.id != cliente.id) {
            throw BusinessRuleException("Número de documento já cadastrado!")
        }
    }
}
