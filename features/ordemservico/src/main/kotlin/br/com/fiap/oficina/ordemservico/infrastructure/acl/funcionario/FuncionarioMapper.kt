package br.com.fiap.oficina.ordemservico.infrastructure.acl.funcionario

import br.com.fiap.oficina.funcionario.application.port.FuncionarioQueryDto
import br.com.fiap.oficina.ordemservico.domain.FuncionarioRef
import br.com.fiap.oficina.ordemservico.domain.port.funcionario.DadosFuncionario
import br.com.fiap.oficina.shared.domain.mapper.QueryMapper

internal class FuncionarioMapper : QueryMapper<FuncionarioQueryDto, DadosFuncionario> {
    override fun toProviderResponse(dto: FuncionarioQueryDto): DadosFuncionario = DadosFuncionario(
        id = FuncionarioRef.toUUID(dto.id),
    )
}
