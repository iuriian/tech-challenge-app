package br.com.fiap.oficina.servico.infrastructure.provider

import br.com.fiap.oficina.servico.application.port.ServicoQueryDto
import br.com.fiap.oficina.servico.domain.Servico
import br.com.fiap.oficina.shared.domain.mapper.QueryMapper

internal class ServicoQueryMapper : QueryMapper<Servico, ServicoQueryDto> {
    override fun toProviderResponse(domain: Servico): ServicoQueryDto = ServicoQueryDto(
        id = domain.id.toString(),
        descricao = domain.descricao,
        valor = domain.valor,
        ativo = domain.ativo,
    )
}
