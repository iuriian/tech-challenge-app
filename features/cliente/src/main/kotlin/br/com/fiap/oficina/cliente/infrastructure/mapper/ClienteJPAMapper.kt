package br.com.fiap.oficina.cliente.infrastructure.mapper

import br.com.fiap.oficina.cliente.domain.Cliente
import br.com.fiap.oficina.cliente.domain.tipoPessoa
import br.com.fiap.oficina.cliente.infrastructure.persistence.ClienteJPA
import br.com.fiap.oficina.cliente.infrastructure.persistence.DocumentoEmbeddable
import br.com.fiap.oficina.shared.domain.mapper.JPAMapper
import br.com.fiap.oficina.shared.domain.valueobject.Document

internal class ClienteJPAMapper(
    private val enderecoMapper: EnderecoJPAMapper = EnderecoJPAMapper(),
    private val contatoMapper: ContatoJPAMapper = ContatoJPAMapper(),
) : JPAMapper<Cliente, ClienteJPA> {
    override fun toDomain(jpa: ClienteJPA): Cliente = Cliente
        .reconstruir(
            id = jpa.id.toString(),
            nome = jpa.nome,
            documento = Document.from(jpa.documento.numero, jpa.documento.tipoPessoa.tipoDocumento),
            email = jpa.email,
            endereco = jpa.endereco?.let { enderecoMapper.toDomain(it) },
            contatos = jpa.contatos.map { contatoMapper.toDomain(it) },
        )

    override fun toJPA(domain: Cliente): ClienteJPA = ClienteJPA(
        id = domain.id.value,
        nome = domain.nome,
        documento = DocumentoEmbeddable(domain.documento.valor, domain.documento.tipoPessoa),
        email = domain.email,
        endereco = domain.endereco?.let { enderecoMapper.toJPA(it) },
        contatos = domain.contatos.map { contatoMapper.toJPA(it) }.toMutableList(),
    )
}
