package br.com.fiap.oficina.peca.application.mapper

import br.com.fiap.oficina.peca.application.dto.PecaRequest
import br.com.fiap.oficina.peca.application.dto.PecaResponse
import br.com.fiap.oficina.peca.domain.Peca
import org.springframework.stereotype.Component

@Component
class PecaMapper {
    fun toDomain(request: PecaRequest): Peca = Peca.criar(
        codigo = request.codigo,
        nome = request.nome,
        descricao = request.descricao,
        fabricante = request.fabricante,
        fornecedor = request.fornecedor,
        precoCompra = request.precoDeCompra,
        precoVenda = request.precoDeVenda,
        quantidade = request.qtdEstoque,
    )

    fun toResponse(peca: Peca): PecaResponse = PecaResponse(
        id = peca.id.toString(),
        codigo = peca.codigo,
        nome = peca.nome,
        descricao = peca.descricao,
        fabricante = peca.fabricante,
        fornecedor = peca.fornecedor,
        precoDeCompra = peca.precoCompra,
        precoDeVenda = peca.precoVenda,
        qtdEstoque = peca.quantidade,
        ativo = peca.ativo,
    )
}
