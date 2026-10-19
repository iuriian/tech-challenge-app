package br.com.fiap.oficina.cliente.application.mapper

import br.com.fiap.oficina.cliente.application.dto.EnderecoRequest
import br.com.fiap.oficina.cliente.application.dto.EnderecoResponse
import br.com.fiap.oficina.cliente.domain.Endereco
import br.com.fiap.oficina.shared.domain.mapper.ApplicationMapper
import org.springframework.stereotype.Component

@Component
internal class EnderecoMapper : ApplicationMapper<Endereco, EnderecoRequest, EnderecoResponse> {
    override fun toDomain(request: EnderecoRequest): Endereco =
        Endereco.criar(
            logradouro = request.logradouro,
            numero = request.numero,
            complemento = request.complemento,
            bairro = request.bairro,
            cidade = request.cidade,
            estado = request.estado,
            cep = request.cep,
        )

    override fun toResponse(domain: Endereco): EnderecoResponse =
        EnderecoResponse(
            logradouro = domain.logradouro,
            numero = domain.numero,
            complemento = domain.complemento,
            bairro = domain.bairro,
            cidade = domain.cidade,
            estado = domain.estado,
            cep = domain.cep,
        )
}
