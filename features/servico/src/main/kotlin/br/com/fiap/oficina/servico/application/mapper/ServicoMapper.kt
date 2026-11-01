package br.com.fiap.oficina.servico.application.mapper

import br.com.fiap.oficina.servico.application.dto.ServicoRequest
import br.com.fiap.oficina.servico.application.dto.ServicoResponse
import br.com.fiap.oficina.servico.domain.Servico
import br.com.fiap.oficina.shared.domain.mapper.ApplicationMapper
import org.springframework.stereotype.Component

@Component
internal class ServicoMapper : ApplicationMapper<Servico, ServicoRequest, ServicoResponse> {
    override fun toDomain(request: ServicoRequest): Servico = Servico.criar(
        descricao = request.descricao,
        valor = request.valor,
    )

    override fun toResponse(domain: Servico): ServicoResponse = ServicoResponse(
        id = domain.id.toString(),
        descricao = domain.descricao,
        valor = domain.valor,
        ativo = domain.ativo,
    )
}
