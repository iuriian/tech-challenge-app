package br.com.fiap.oficina.peca.application.usecase

import br.com.fiap.oficina.peca.application.dto.PecaRequest
import br.com.fiap.oficina.peca.application.dto.PecaResponse

interface AtualizarPecaUseCase {
    fun executar(id: String, request: PecaRequest): PecaResponse
}
