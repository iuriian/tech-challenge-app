package br.com.fiap.oficina.funcionario.infrastructure.mapper

import br.com.fiap.oficina.funcionario.domain.Funcionario
import br.com.fiap.oficina.funcionario.infrastructure.persistence.FuncionarioJPA

internal class FuncionarioJPAMapper {
    fun toDomain(jpa: FuncionarioJPA): Funcionario =
        Funcionario
            .reconstruir(
                id = jpa.id.toString(),
                nome = jpa.nome,
                cpf = jpa.cpf,
                cargo = jpa.cargo.descricao,
            )

    fun toJPA(entity: Funcionario): FuncionarioJPA =
        FuncionarioJPA(
            id = entity.id.value,
            nome = entity.nome,
            cpf = entity.cpf.value,
            cargo = entity.cargo,
        )
}
