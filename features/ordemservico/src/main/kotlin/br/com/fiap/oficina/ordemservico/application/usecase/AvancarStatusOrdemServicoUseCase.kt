package br.com.fiap.oficina.ordemservico.application.usecase

import br.com.fiap.oficina.ordemservico.application.dto.OrdemServicoResponse

interface AvancarStatusOrdemServicoUseCase {
    fun executar(id: String): OrdemServicoResponse
}
