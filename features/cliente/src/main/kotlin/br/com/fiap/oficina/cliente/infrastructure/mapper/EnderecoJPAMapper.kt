package br.com.fiap.oficina.cliente.infrastructure.mapper

import br.com.fiap.oficina.cliente.domain.Endereco
import br.com.fiap.oficina.cliente.infrastructure.persistence.EnderecoJPA
import br.com.fiap.oficina.shared.domain.mapper.JPAMapper

internal class EnderecoJPAMapper : JPAMapper<Endereco, EnderecoJPA> {
    override fun toDomain(jpa: EnderecoJPA): Endereco = Endereco
        .reconstruir(
            id = jpa.id.toString(),
            logradouro = jpa.logradouro,
            numero = jpa.numero,
            complemento = jpa.complemento,
            bairro = jpa.bairro,
            cidade = jpa.cidade,
            estado = jpa.estado,
            cep = jpa.cep,
        )

    override fun toJPA(domain: Endereco): EnderecoJPA = EnderecoJPA(
        id = domain.id.value,
        logradouro = domain.logradouro,
        numero = domain.numero,
        complemento = domain.complemento,
        bairro = domain.bairro,
        cidade = domain.cidade,
        estado = domain.estado,
        cep = domain.cep,
    )
}
