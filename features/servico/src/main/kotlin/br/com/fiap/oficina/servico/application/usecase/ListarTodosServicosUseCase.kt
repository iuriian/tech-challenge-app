package br.com.fiap.oficina.servico.application.usecase

import br.com.fiap.oficina.servico.application.dto.ServicoResponse

interface ListarTodosServicosUseCase {
    fun executar(): List<ServicoResponse>
}
