package br.com.fiap.oficina.cliente.application.usecase

import br.com.fiap.oficina.cliente.application.dto.ClienteResponse

interface BuscarClientePorNomeUseCase {
    fun executar(nome: String): ClienteResponse
}
