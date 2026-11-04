package br.com.fiap.oficina.funcionario.infrastructure.mapper

import br.com.fiap.oficina.funcionario.domain.Funcionario
import br.com.fiap.oficina.funcionario.infrastructure.persistence.FuncionarioJPA
import br.com.fiap.oficina.shared.domain.mapper.JPAMapper
import br.com.fiap.oficina.shared.domain.valueobject.CPF

internal class FuncionarioJPAMapper : JPAMapper<Funcionario, FuncionarioJPA> {
    override fun toDomain(jpa: FuncionarioJPA): Funcionario = Funcionario
        .reconstruir(
            id = jpa.id.toString(),
            nome = jpa.nome,
            cpf = CPF(jpa.cpf),
            cargo = jpa.cargo.descricao,
        )

    override fun toJPA(domain: Funcionario): FuncionarioJPA = FuncionarioJPA(
        id = domain.id.value,
        nome = domain.nome,
        cpf = domain.cpf.valor,
        cargo = domain.cargo,
    )
}
