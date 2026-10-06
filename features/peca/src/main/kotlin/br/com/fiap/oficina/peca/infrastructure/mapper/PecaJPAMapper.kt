package br.com.fiap.oficina.peca.infrastructure.mapper

import br.com.fiap.oficina.peca.domain.Peca
import br.com.fiap.oficina.peca.infrastructure.persistence.PecaJPA

internal class PecaJPAMapper {
    fun toDomain(jpa: PecaJPA): Peca = Peca.reconstruir(
        id = jpa.id.toString(),
        codigo = jpa.codigo,
        nome = jpa.nome,
        descricao = jpa.descricao,
        fabricante = jpa.fabricante,
        fornecedor = jpa.fornecedor,
        precoCompra = jpa.precoCompra,
        precoVenda = jpa.precoVenda,
        quantidade = jpa.quantidade,
        ativo = jpa.ativo,
    )

    fun toJpa(domain: Peca): PecaJPA = PecaJPA(
        id = domain.id.value,
        codigo = domain.codigo,
        nome = domain.nome,
        descricao = domain.descricao,
        fabricante = domain.fabricante,
        fornecedor = domain.fornecedor,
        precoCompra = domain.precoCompra,
        precoVenda = domain.precoVenda,
        quantidade = domain.quantidade,
        ativo = domain.ativo,
    )
}
