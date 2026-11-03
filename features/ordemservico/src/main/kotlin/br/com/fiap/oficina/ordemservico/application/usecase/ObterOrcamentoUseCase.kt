package br.com.fiap.oficina.ordemservico.application.usecase

import br.com.fiap.oficina.ordemservico.application.dto.OrcamentoResponse

interface ObterOrcamentoUseCase {
    fun executar(id: String): OrcamentoResponse
}
