package br.com.fiap.oficina.cliente.infrastructure.mapper

import br.com.fiap.oficina.cliente.domain.Contato
import br.com.fiap.oficina.cliente.infrastructure.persistence.ContatoJPA
import br.com.fiap.oficina.shared.domain.mapper.JPAMapper

internal class ContatoJPAMapper : JPAMapper<Contato, ContatoJPA> {
    override fun toDomain(jpa: ContatoJPA): Contato =
        Contato.reconstruir(
            id = jpa.id.toString(),
            tipo = jpa.tipo,
            nome = jpa.nome,
            telefone = jpa.telefone,
        )

    override fun toJPA(domain: Contato): ContatoJPA =
        ContatoJPA(
            id = domain.id.value,
            tipo = domain.tipo,
            nome = domain.nome,
            telefone = domain.telefone,
        )
}
