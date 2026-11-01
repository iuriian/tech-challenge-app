package br.com.fiap.oficina.cliente.application.usecase

import br.com.fiap.oficina.cliente.application.dto.ClienteResponse

interface BuscarClientePorDocumentoUseCase {
    fun executar(numeroDocumento: String): ClienteResponse
}
