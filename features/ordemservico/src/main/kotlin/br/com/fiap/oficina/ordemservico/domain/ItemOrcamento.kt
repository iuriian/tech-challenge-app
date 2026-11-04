package br.com.fiap.oficina.ordemservico.domain

import br.com.fiap.oficina.ordemservico.domain.port.peca.DadosPeca
import br.com.fiap.oficina.ordemservico.domain.port.servico.DadosServico
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Item do orçamento, com o preço praticado no momento em que foi incluído.
 *
 * Valores e quantidades são normalizados em [ESCALA] casas: é a escala em que as
 * colunas `NUMERIC(10,2)` devolvem os dados, e sem isso um item recém-criado não
 * seria igual ao mesmo item relido do banco — [BigDecimal.equals] compara a escala.
 */
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
        get() = valorUnitario.multiply(quantidade).normalizar()

    companion object {
        /** Casas decimais em que valores e quantidades são mantidos. */
        const val ESCALA = 2

        fun dePeca(peca: DadosPeca, quantidade: BigDecimal): ItemOrcamento = ItemOrcamento(
            tipo = TipoItemOrcamento.PECA,
            referenciaId = peca.id,
            descricao = peca.nome,
            valorUnitario = peca.precoVenda.normalizar(),
            quantidade = quantidade.normalizar(),
            codigoReferencia = peca.codigo,
        )

        fun deServico(servico: DadosServico, quantidade: BigDecimal = BigDecimal.ONE): ItemOrcamento = ItemOrcamento(
            tipo = TipoItemOrcamento.SERVICO,
            referenciaId = servico.id,
            descricao = servico.descricao,
            valorUnitario = servico.valor.normalizar(),
            quantidade = quantidade.normalizar(),
        )

        private fun BigDecimal.normalizar(): BigDecimal = setScale(ESCALA, RoundingMode.HALF_UP)
    }
}
