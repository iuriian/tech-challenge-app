package br.com.fiap.oficina.ordemservico.infrastructure.acl.peca

import br.com.fiap.oficina.ordemservico.domain.ItemReferenciaRef
import br.com.fiap.oficina.ordemservico.domain.port.peca.DadosPeca
import br.com.fiap.oficina.peca.application.port.PecaQueryDto
import br.com.fiap.oficina.shared.domain.mapper.QueryMapper

internal class PecaMapper : QueryMapper<PecaQueryDto, DadosPeca> {
    override fun toProviderResponse(dto: PecaQueryDto): DadosPeca = DadosPeca(
        id = ItemReferenciaRef.toUUID(dto.id),
        codigo = dto.codigo,
        nome = dto.nome,
        precoVenda = dto.precoVenda,
        ativo = dto.ativo,
    )
}
