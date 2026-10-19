package br.com.fiap.oficina.cliente.application.mapper

import br.com.fiap.oficina.cliente.application.dto.ClienteRequest
import br.com.fiap.oficina.cliente.application.dto.ClienteResponse
import br.com.fiap.oficina.cliente.domain.Cliente
import br.com.fiap.oficina.cliente.domain.TipoPessoa
import br.com.fiap.oficina.cliente.domain.tipoPessoa
import br.com.fiap.oficina.shared.domain.mapper.ApplicationMapper
import br.com.fiap.oficina.shared.domain.valueobject.Document
import org.springframework.stereotype.Component

@Component
internal class ClienteMapper(
    private val enderecoMapper: EnderecoMapper,
    private val contatoMapper: ContatoMapper,
) : ApplicationMapper<Cliente, ClienteRequest, ClienteResponse> {
    override fun toDomain(request: ClienteRequest): Cliente =
        Cliente.criar(
            nome = request.nome,
            documento =
                Document.from(
                    valor = request.numeroDocumento,
                    tipo = TipoPessoa.valueOf(request.tipoPessoa).tipoDocumento,
                ),
            email = request.email,
            endereco = request.endereco?.let(enderecoMapper::toDomain),
            contatos = request.contatos.map(contatoMapper::toDomain),
        )

    override fun toResponse(domain: Cliente): ClienteResponse =
        ClienteResponse(
            id = domain.id.toString(),
            nome = domain.nome,
            numeroDocumento = domain.documento.formatado(),
            email = domain.email,
            endereco = domain.endereco?.let(enderecoMapper::toResponse),
            contatos = domain.contatos.map(contatoMapper::toResponse),
            tipoPessoa = domain.documento.tipoPessoa.name,
        )
}
