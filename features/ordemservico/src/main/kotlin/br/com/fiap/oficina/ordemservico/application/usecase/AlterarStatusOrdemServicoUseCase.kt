package br.com.fiap.oficina.ordemservico.application.usecase

import br.com.fiap.oficina.ordemservico.application.dto.AlterarStatusRequest
import br.com.fiap.oficina.ordemservico.application.dto.OrdemServicoResponse

interface AlterarStatusOrdemServicoUseCase {
    fun executar(id: String, request: AlterarStatusRequest): OrdemServicoResponse
}
