package br.com.fiap.oficina.peca.application.usecase

import br.com.fiap.oficina.peca.application.dto.PecaResponse
import br.com.fiap.oficina.peca.application.dto.StatusPecaRequest

interface AtualizarStatusPecaUseCase {
    fun executar(
        codigo: String,
        status: StatusPecaRequest,
    ): PecaResponse
}
