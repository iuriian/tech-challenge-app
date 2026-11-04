package br.com.fiap.oficina.peca.application.usecase

import br.com.fiap.oficina.peca.application.dto.PecaResponse

interface ListarPecasUseCase {
    fun executar(): List<PecaResponse>
}
