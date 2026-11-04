package br.com.fiap.oficina.ordemservico.infrastructure.acl.servico

import br.com.fiap.oficina.ordemservico.domain.ItemReferenciaRef
import br.com.fiap.oficina.ordemservico.domain.port.servico.ConsultarServico
import br.com.fiap.oficina.ordemservico.domain.port.servico.DadosServico
import br.com.fiap.oficina.servico.application.port.ServicoQueryPort

internal class ConsultarServicoAdapter(private val queryPort: ServicoQueryPort, private val mapper: ServicoMapper) :
    ConsultarServico {
    override fun buscarPorId(ref: ItemReferenciaRef): DadosServico? =
        queryPort.buscarPorId(ref.value)?.let(mapper::toProviderResponse)
}
