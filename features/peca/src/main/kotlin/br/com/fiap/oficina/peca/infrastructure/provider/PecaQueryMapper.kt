package br.com.fiap.oficina.peca.infrastructure.provider

import br.com.fiap.oficina.peca.application.port.PecaQueryDto
import br.com.fiap.oficina.peca.domain.Peca
import br.com.fiap.oficina.shared.domain.mapper.QueryMapper

internal class PecaQueryMapper : QueryMapper<Peca, PecaQueryDto> {
    override fun toProviderResponse(domain: Peca): PecaQueryDto = PecaQueryDto(
        id = domain.id.toString(),
        codigo = domain.codigo,
        nome = domain.nome,
        precoVenda = domain.precoVenda,
        ativo = domain.ativo,
    )
}
