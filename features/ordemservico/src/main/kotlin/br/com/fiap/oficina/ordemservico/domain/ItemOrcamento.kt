package br.com.fiap.oficina.ordemservico.domain

import br.com.fiap.oficina.ordemservico.domain.port.peca.DadosPeca
import br.com.fiap.oficina.ordemservico.domain.port.servico.DadosServico
import java.math.BigDecimal

data class ItemOrcamento(
    val tipo: TipoItemOrcamento,
    val referenciaId: ItemReferenciaRef,
    val descricao: String,
    val valorUnitario: BigDecimal,
    val quantidade: BigDecimal,
    val codigoReferencia: String? = null,
) {
    init {
        require(descricao.isNotBlank()) {
            "Descrição do item do orçamento é obrigatória"
        }
        require(valorUnitario >= BigDecimal.ZERO) {
            "Valor unitário do item não pode ser negativo"
        }
        require(quantidade > BigDecimal.ZERO) {
            "Quantidade do item deve ser maior que zero"
        }
        require(tipo != TipoItemOrcamento.PECA || !codigoReferencia.isNullOrBlank()) {
            "Código da peça é obrigatório"
        }
    }

    val subtotal: BigDecimal
        get() = valorUnitario.multiply(quantidade)

    companion object {
        fun dePeca(peca: DadosPeca, quantidade: BigDecimal): ItemOrcamento = ItemOrcamento(
            tipo = TipoItemOrcamento.PECA,
            referenciaId = peca.id,
            descricao = peca.nome,
            valorUnitario = peca.precoVenda,
            quantidade = quantidade,
            codigoReferencia = peca.codigo,
        )

        fun deServico(servico: DadosServico, quantidade: BigDecimal = BigDecimal.ONE): ItemOrcamento = ItemOrcamento(
            tipo = TipoItemOrcamento.SERVICO,
            referenciaId = servico.id,
            descricao = servico.descricao,
            valorUnitario = servico.valor,
            quantidade = quantidade,
        )
    }
}
