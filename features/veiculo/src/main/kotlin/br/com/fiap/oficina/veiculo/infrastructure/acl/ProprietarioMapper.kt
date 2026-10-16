package br.com.fiap.oficina.veiculo.infrastructure.acl

import br.com.fiap.oficina.cliente.application.port.ClienteQueryDto
import br.com.fiap.oficina.shared.domain.mapper.QueryMapper
import br.com.fiap.oficina.veiculo.domain.ProprietarioRef
import br.com.fiap.oficina.veiculo.domain.port.DadosProprietario

internal class ProprietarioMapper : QueryMapper<ClienteQueryDto, DadosProprietario> {
    override fun toProviderResponse(dto: ClienteQueryDto): DadosProprietario =
        DadosProprietario(
            ref = ProprietarioRef.toUUID(dto.id),
            nome = dto.nome,
            numeroDocumento = dto.numeroDocumento,
        )
}
