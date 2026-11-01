package br.com.fiap.oficina.servico.application.usecase

import br.com.fiap.oficina.servico.application.dto.ServicoRequest
import br.com.fiap.oficina.servico.application.dto.ServicoResponse

interface AtualizarServicoUseCase {
    fun executar(id: String, request: ServicoRequest): ServicoResponse
}
