package br.com.fiap.oficina.ordemservico.application.usecase

import br.com.fiap.oficina.ordemservico.application.dto.OrdemServicoResponse

interface BuscarOrdemServicoPorClienteUseCase {
    fun executar(id: String): List<OrdemServicoResponse>
}
