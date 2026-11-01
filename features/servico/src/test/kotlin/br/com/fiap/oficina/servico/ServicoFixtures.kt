package br.com.fiap.oficina.servico

import br.com.fiap.oficina.servico.application.dto.ServicoRequest
import br.com.fiap.oficina.servico.application.dto.ServicoResponse
import br.com.fiap.oficina.servico.domain.Servico
import br.com.fiap.oficina.servico.infrastructure.persistence.ServicoJPA
import java.math.BigDecimal
import java.util.UUID

/** Massa de teste compartilhada pelos testes de serviço. */
internal object ServicoFixtures {
    const val ID_1 = "00000000-0000-0000-0000-000000000100"
    const val ID_2 = "00000000-0000-0000-0000-000000000200"

    const val DESCRICAO = "Troca de óleo"
    const val DESCRICAO_ALTERNATIVA = "Alinhamento e balanceamento"

    val VALOR: BigDecimal = BigDecimal("150.00")
    val VALOR_ALTERNATIVO: BigDecimal = BigDecimal("220.50")

    fun servico(id: String = ID_1, descricao: String = DESCRICAO, valor: BigDecimal = VALOR, ativo: Boolean = true) =
        Servico.reconstruir(
            id = id,
            descricao = descricao,
            valor = valor,
            ativo = ativo,
        )

    fun novoServico(descricao: String = DESCRICAO, valor: BigDecimal = VALOR) = Servico.criar(
        descricao = descricao,
        valor = valor,
    )

    fun request(descricao: String = DESCRICAO, valor: BigDecimal = VALOR) = ServicoRequest(
        descricao = descricao,
        valor = valor,
    )

    fun response(servico: Servico) = ServicoResponse(
        id = servico.id.toString(),
        descricao = servico.descricao,
        valor = servico.valor,
        ativo = servico.ativo,
    )

    fun servicoJPA(
        id: String = ID_1,
        descricao: String = DESCRICAO,
        valor: BigDecimal = VALOR,
        ativo: Boolean = true,
    ) = ServicoJPA(
        id = UUID.fromString(id),
        descricao = descricao,
        valor = valor,
        ativo = ativo,
    )
}
