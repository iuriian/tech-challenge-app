package br.com.fiap.oficina.ordemservico.infrastructure.persistence

import br.com.fiap.oficina.ordemservico.domain.SequenciaOrdemServico
import jakarta.persistence.EntityManager

internal class SequenciaOrdemServicoImpl(private val entityManager: EntityManager) : SequenciaOrdemServico {
    override fun proximoValor(): Long = (
        entityManager
            .createNativeQuery(PROXIMO_VALOR)
            .singleResult as Number
        ).toLong()

    private companion object {
        const val PROXIMO_VALOR = "SELECT nextval('ordem_servico_numero_seq')"
    }
}
