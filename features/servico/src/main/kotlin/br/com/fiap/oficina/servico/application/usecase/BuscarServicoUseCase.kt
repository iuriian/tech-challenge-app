package br.com.fiap.oficina.servico.application.usecase

import br.com.fiap.oficina.servico.application.dto.ServicoResponse

interface BuscarServicoUseCase {
    fun executar(id: String): ServicoResponse
}
