package br.com.fiap.oficina.ordemservico.application.usecase

import br.com.fiap.oficina.ordemservico.application.dto.OrdemServicoRequest
import br.com.fiap.oficina.ordemservico.application.dto.OrdemServicoResponse

interface CriarOrdemServicoUseCase {
    fun executar(request: OrdemServicoRequest): OrdemServicoResponse
}
