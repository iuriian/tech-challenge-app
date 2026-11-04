package br.com.fiap.oficina.cliente.application.usecase

import br.com.fiap.oficina.cliente.application.dto.ClienteRequest
import br.com.fiap.oficina.cliente.application.dto.ClienteResponse

interface CriarClienteUseCase {
    fun executar(request: ClienteRequest): ClienteResponse
}
