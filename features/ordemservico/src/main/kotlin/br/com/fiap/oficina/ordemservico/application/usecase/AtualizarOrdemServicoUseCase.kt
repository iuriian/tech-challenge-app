package br.com.fiap.oficina.ordemservico.application.usecase

import br.com.fiap.oficina.ordemservico.application.dto.OrdemServicoRequest
import br.com.fiap.oficina.ordemservico.application.dto.OrdemServicoResponse

interface AtualizarOrdemServicoUseCase {
    fun executar(id: String, request: OrdemServicoRequest): OrdemServicoResponse
}
