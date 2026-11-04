package br.com.fiap.oficina.funcionario.infrastructure.provider

import br.com.fiap.oficina.funcionario.application.port.FuncionarioQueryDto
import br.com.fiap.oficina.funcionario.domain.Funcionario
import br.com.fiap.oficina.shared.domain.mapper.QueryMapper

internal class FuncionarioQueryMapper : QueryMapper<Funcionario, FuncionarioQueryDto> {
    override fun toProviderResponse(domain: Funcionario): FuncionarioQueryDto = FuncionarioQueryDto(
        id = domain.id.toString(),
        nome = domain.nome,
        cargo = domain.cargo.descricao,
    )
}
