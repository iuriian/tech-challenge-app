package br.com.fiap.oficina.cliente.application.mapper

import br.com.fiap.oficina.cliente.application.dto.ContatoRequest
import br.com.fiap.oficina.cliente.application.dto.ContatoResponse
import br.com.fiap.oficina.cliente.domain.Contato
import br.com.fiap.oficina.shared.domain.mapper.ApplicationMapper
import org.springframework.stereotype.Component

@Component
internal class ContatoMapper : ApplicationMapper<Contato, ContatoRequest, ContatoResponse> {
    override fun toDomain(request: ContatoRequest): Contato = Contato.criar(
        tipo = request.tipo,
        nome = request.nome,
        telefone = request.telefone,
    )

    override fun toResponse(domain: Contato): ContatoResponse = ContatoResponse(
        tipo = domain.tipo,
        nome = domain.nome,
        telefone = domain.telefone,
    )
}
