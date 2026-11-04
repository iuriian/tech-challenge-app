package br.com.fiap.oficina.ordemservico.infrastructure.acl.peca

import br.com.fiap.oficina.ordemservico.domain.ItemReferenciaRef
import br.com.fiap.oficina.ordemservico.domain.port.peca.ConsultarPeca
import br.com.fiap.oficina.ordemservico.domain.port.peca.DadosPeca
import br.com.fiap.oficina.peca.application.port.PecaQueryPort

internal class ConsultarPecaAdapter(private val queryPort: PecaQueryPort, private val mapper: PecaMapper) :
    ConsultarPeca {
    override fun buscarPorId(ref: ItemReferenciaRef): DadosPeca? =
        queryPort.buscarPorId(ref.value)?.let(mapper::toProviderResponse)
}
