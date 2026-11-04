package br.com.fiap.oficina.peca.application.usecase

import br.com.fiap.oficina.peca.application.dto.PecaRequest
import br.com.fiap.oficina.peca.application.dto.PecaResponse

interface CadastrarPecaUseCase {
    fun executar(request: PecaRequest): PecaResponse
}
