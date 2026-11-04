package br.com.fiap.oficina.servico.infrastructure.mapper

import br.com.fiap.oficina.servico.domain.Servico
import br.com.fiap.oficina.servico.infrastructure.persistence.ServicoJPA
import br.com.fiap.oficina.shared.domain.mapper.JPAMapper

internal class ServicoJPAMapper : JPAMapper<Servico, ServicoJPA> {
    override fun toDomain(jpa: ServicoJPA): Servico = Servico.reconstruir(
        id = jpa.id.toString(),
        descricao = jpa.descricao,
        valor = jpa.valor,
        ativo = jpa.ativo,
    )

    override fun toJPA(domain: Servico): ServicoJPA = ServicoJPA(
        id = domain.id.value,
        descricao = domain.descricao,
        valor = domain.valor,
        ativo = domain.ativo,
    )
}
