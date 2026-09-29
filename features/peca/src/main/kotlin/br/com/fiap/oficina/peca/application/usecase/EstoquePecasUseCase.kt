package br.com.fiap.oficina.peca.application.usecase

import br.com.fiap.oficina.peca.application.dto.EstoquePecaRequest
import br.com.fiap.oficina.peca.application.dto.PecaResponse

interface EstoquePecasUseCase {
    fun executar(
        codigo: String,
        request: EstoquePecaRequest,
    ): PecaResponse
}
