package br.com.fiap.oficina.peca.application.usecase

import br.com.fiap.oficina.peca.application.dto.PecaResponse

interface BuscarPecaPorCodigoUseCase {
    fun executar(codigo: String): PecaResponse
}
