package br.com.fiap.oficina.peca.application.usecase

import br.com.fiap.oficina.peca.application.dto.PecaResponse

interface BuscarPecaPorIdUseCase {
    fun executar(id: String): PecaResponse
}
