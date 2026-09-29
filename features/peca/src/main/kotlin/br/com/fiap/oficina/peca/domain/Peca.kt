package br.com.fiap.oficina.peca.domain

import java.math.BigDecimal

@ConsistentCopyVisibility
data class Peca private constructor(
    val id: PecaId,
    val codigo: String,
    val nome: String,
    val descricao: String = "",
    val fabricante: String,
    val fornecedor: String = "",
    val precoCompra: BigDecimal = BigDecimal.ZERO,
    val precoVenda: BigDecimal = BigDecimal.ZERO,
    val quantidade: Int = 0,
    val ativo: Boolean = false,
) {
    init {
        require(codigo.isNotBlank()) { "Código da peça é obrigatório" }
        require(nome.isNotBlank()) { "Nome da peça é obrigatório" }
        require(precoCompra >= BigDecimal.ZERO) { "Preço de compra não pode ser menor que zero" }
        require(precoVenda >= BigDecimal.ZERO) { "Preço de venda não pode ser menor que zero" }
        require(quantidade >= 0) { "Quantidade em estoque não pode ser menor que zero" }
    }

    companion object {
        fun criar(
            codigo: String,
            nome: String,
            descricao: String? = null,
            fabricante: String,
            fornecedor: String? = null,
            precoCompra: BigDecimal,
            precoVenda: BigDecimal,
            quantidade: Int,
        ): Peca =
            Peca(
                id = PecaId.generate(),
                codigo = codigo,
                nome = nome,
                descricao = descricao.orEmpty(),
                fabricante = fabricante,
                fornecedor = fornecedor.orEmpty(),
                precoCompra = precoCompra,
                precoVenda = precoVenda,
                quantidade = quantidade,
            )

        fun reconstruir(
            id: String,
            codigo: String,
            nome: String,
            descricao: String,
            fabricante: String,
            fornecedor: String,
            precoCompra: BigDecimal,
            precoVenda: BigDecimal,
            quantidade: Int,
            ativo: Boolean,
        ): Peca =
            Peca(
                id = PecaId.toUUID(id),
                codigo = codigo,
                nome = nome,
                descricao = descricao,
                fabricante = fabricante,
                fornecedor = fornecedor,
                precoCompra = precoCompra,
                precoVenda = precoVenda,
                quantidade = quantidade,
                ativo = ativo,
            )
    }

    fun atualizar(
        nome: String,
        codigo: String,
        descricao: String? = null,
        fabricante: String,
        fornecedor: String? = null,
        precoCompra: BigDecimal,
        precoVenda: BigDecimal,
    ): Peca =
        this.copy(
            nome = nome,
            codigo = codigo,
            descricao = descricao.orEmpty(),
            fabricante = fabricante,
            fornecedor = fornecedor.orEmpty(),
            precoCompra = precoCompra,
            precoVenda = precoVenda,
        )

    fun ativar(): Peca = copy(ativo = true)

    fun desativar(): Peca = copy(ativo = false)

    fun atualizarEstoque(qtd: Int): Peca {
        require(qtd >= 0) { "Quantidade em estoque não pode ser negativa" }
        return copy(quantidade = qtd)
    }

    fun retirarPecas(qtd: Int): Peca {
        require(qtd > 0) { "Quantidade para retirada deve ser maior que zero" }
        require(qtd <= quantidade) { "Quantidade em estoque insuficiente" }
        return copy(quantidade = quantidade - qtd)
    }

    fun reporPecas(qtd: Int): Peca {
        require(qtd > 0) { "Quantidade para reposição deve ser maior que zero" }
        return copy(quantidade = quantidade + qtd)
    }
}
