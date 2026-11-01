package br.com.fiap.oficina.servico.application.usecase

import br.com.fiap.oficina.servico.application.dto.ServicoRequest
import br.com.fiap.oficina.servico.application.dto.ServicoResponse

interface CriarServicoUseCase {
    fun executar(request: ServicoRequest): ServicoResponse
}
