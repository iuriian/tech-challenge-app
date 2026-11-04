package br.com.fiap.oficina.ordemservico.domain.port.servico

import br.com.fiap.oficina.ordemservico.domain.ItemReferenciaRef

interface ConsultarServico {
    fun buscarPorId(ref: ItemReferenciaRef): DadosServico?
}
