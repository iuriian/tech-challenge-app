package br.com.fiap.oficina.ordemservico.infrastructure.acl.servico

import br.com.fiap.oficina.ordemservico.domain.ItemReferenciaRef
import br.com.fiap.oficina.ordemservico.domain.port.servico.DadosServico
import br.com.fiap.oficina.servico.application.port.ServicoQueryDto
import br.com.fiap.oficina.shared.domain.mapper.QueryMapper

internal class ServicoMapper : QueryMapper<ServicoQueryDto, DadosServico> {
    override fun toProviderResponse(dto: ServicoQueryDto): DadosServico = DadosServico(
        id = ItemReferenciaRef.toUUID(dto.id),
        descricao = dto.descricao,
        valor = dto.valor,
        ativo = dto.ativo,
    )
}
