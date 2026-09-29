package br.com.fiap.oficina.peca.application.usecase

import br.com.fiap.oficina.peca.application.dto.PecaRequest
import br.com.fiap.oficina.peca.application.dto.PecaResponse
import br.com.fiap.oficina.peca.domain.Peca
import java.math.BigDecimal

/** Massa de teste compartilhada pelos casos de uso de peça. */
internal object PecaFixtures {
    const val ID_1 = "00000000-0000-0000-0000-000000000100"
    const val ID_2 = "00000000-0000-0000-0000-000000000200"
    const val CODIGO = "PC-001"
    const val NOME = "Pastilha de freio"
    const val DESCRICAO = "Pastilha dianteira"
    const val FABRICANTE = "Bosch"
    const val FORNECEDOR = "AutoPeças RJ"
    val PRECO_COMPRA: BigDecimal = BigDecimal("50.00")
    val PRECO_VENDA: BigDecimal = BigDecimal("89.90")

    fun peca(
        id: String = ID_1,
        codigo: String = CODIGO,
        nome: String = NOME,
        quantidade: Int = 10,
        ativo: Boolean = false,
    ) = Peca.reconstruir(
        id = id,
        codigo = codigo,
        nome = nome,
        descricao = DESCRICAO,
        fabricante = FABRICANTE,
        fornecedor = FORNECEDOR,
        precoCompra = PRECO_COMPRA,
        precoVenda = PRECO_VENDA,
        quantidade = quantidade,
        ativo = ativo,
    )

    fun request(
        codigo: String = CODIGO,
        nome: String = NOME,
        quantidade: Int = 10,
    ) = PecaRequest(
        codigo = codigo,
        nome = nome,
        descricao = DESCRICAO,
        fabricante = FABRICANTE,
        fornecedor = FORNECEDOR,
        precoDeCompra = PRECO_COMPRA,
        precoDeVenda = PRECO_VENDA,
        qtdEstoque = quantidade,
    )

    fun response(
        peca: Peca,
    ) = PecaResponse(
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
