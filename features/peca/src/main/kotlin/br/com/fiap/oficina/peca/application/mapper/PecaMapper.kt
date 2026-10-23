package br.com.fiap.oficina.peca.application.mapper

import br.com.fiap.oficina.peca.application.dto.PecaRequest
import br.com.fiap.oficina.peca.application.dto.PecaResponse
import br.com.fiap.oficina.peca.domain.Peca
import br.com.fiap.oficina.shared.domain.mapper.ApplicationMapper
import org.springframework.stereotype.Component

@Component
internal class PecaMapper : ApplicationMapper<Peca, PecaRequest, PecaResponse> {
    override fun toDomain(request: PecaRequest): Peca = Peca.criar(
        codigo = request.codigo,
        nome = request.nome,
        descricao = request.descricao,
        fabricante = request.fabricante,
        fornecedor = request.fornecedor,
        precoCompra = request.precoDeCompra,
        precoVenda = request.precoDeVenda,
        quantidade = request.qtdEstoque,
    )

    override fun toResponse(domain: Peca): PecaResponse = PecaResponse(
        id = domain.id.toString(),
        codigo = domain.codigo,
        nome = domain.nome,
        descricao = domain.descricao,
        fabricante = domain.fabricante,
        fornecedor = domain.fornecedor,
        precoDeCompra = domain.precoCompra,
        precoDeVenda = domain.precoVenda,
        qtdEstoque = domain.quantidade,
        ativo = domain.ativo,
    )
}
