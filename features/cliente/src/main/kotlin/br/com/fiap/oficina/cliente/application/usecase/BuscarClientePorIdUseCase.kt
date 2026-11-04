package br.com.fiap.oficina.cliente.application.usecase

import br.com.fiap.oficina.cliente.application.dto.ClienteResponse

interface BuscarClientePorIdUseCase {
    fun executar(id: String): ClienteResponse
}
