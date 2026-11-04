package br.com.fiap.oficina.ordemservico.domain.port.peca

import br.com.fiap.oficina.ordemservico.domain.ItemReferenciaRef

interface ConsultarPeca {
    fun buscarPorId(ref: ItemReferenciaRef): DadosPeca?
}
