package br.com.fiap.oficina.ordemservico.application.resolver

import br.com.fiap.oficina.ordemservico.application.dto.ItemOrcamentoRequest
import br.com.fiap.oficina.ordemservico.domain.ItemOrcamento
import br.com.fiap.oficina.ordemservico.domain.ItemReferenciaRef
import br.com.fiap.oficina.ordemservico.domain.Orcamento
import br.com.fiap.oficina.ordemservico.domain.TipoItemOrcamento
import br.com.fiap.oficina.ordemservico.domain.port.peca.ConsultarPeca
import br.com.fiap.oficina.ordemservico.domain.port.peca.DadosPeca
import br.com.fiap.oficina.ordemservico.domain.port.servico.ConsultarServico
import br.com.fiap.oficina.ordemservico.domain.port.servico.DadosServico
import br.com.fiap.oficina.shared.domain.exception.BusinessRuleException
import org.springframework.stereotype.Component

/**
 * Monta o orçamento a partir dos itens informados, resolvendo peças e serviços
 * nos contextos de origem.
 *
 * A descrição e o valor unitário são copiados no momento da montagem: o orçamento
 * guarda o preço praticado na abertura da ordem de serviço, não o preço corrente.
 */
@Component
internal class OrcamentoResolver(
    private val consultarPeca: ConsultarPeca,
    private val consultarServico: ConsultarServico,
) {
    fun resolver(itens: List<ItemOrcamentoRequest>): Orcamento = Orcamento.criar(itens.map(::resolverItem))

    private fun resolverItem(item: ItemOrcamentoRequest): ItemOrcamento = when (item.tipo) {
        TipoItemOrcamento.PECA -> ItemOrcamento.dePeca(
            peca = buscarPeca(item.referenciaId),
            quantidade = item.quantidade,
        )

        TipoItemOrcamento.SERVICO -> ItemOrcamento.deServico(
            servico = buscarServico(item.referenciaId),
            quantidade = item.quantidade,
        )
    }

    private fun buscarPeca(referenciaId: String): DadosPeca {
        val peca =
            consultarPeca.buscarPorId(ItemReferenciaRef.toUUID(referenciaId))
                ?: throw BusinessRuleException("Peça não encontrada com o id: $referenciaId")

        if (!peca.ativo) {
            throw BusinessRuleException("Peça inativa não pode compor o orçamento, id: $referenciaId")
        }

        return peca
    }

    private fun buscarServico(referenciaId: String): DadosServico {
        val servico =
            consultarServico.buscarPorId(ItemReferenciaRef.toUUID(referenciaId))
                ?: throw BusinessRuleException("Serviço não encontrado com o id: $referenciaId")

        if (!servico.ativo) {
            throw BusinessRuleException("Serviço inativo não pode compor o orçamento, id: $referenciaId")
        }

        return servico
    }
}
