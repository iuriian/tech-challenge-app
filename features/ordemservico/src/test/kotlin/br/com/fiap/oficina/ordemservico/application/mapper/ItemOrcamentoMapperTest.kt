package br.com.fiap.oficina.ordemservico.application.mapper

import br.com.fiap.oficina.ordemservico.OrdemServicoFixtures
import br.com.fiap.oficina.ordemservico.domain.TipoItemOrcamento
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.math.BigDecimal

@DisplayName("Aplicação - Mapper de Item do Orçamento")
class ItemOrcamentoMapperTest {
    private val mapper = ItemOrcamentoMapper()

    @Test
    @DisplayName("Dado um item de peça, quando converter para response, então deve mapear todos os campos")
    fun givenPecaItem_whenConvertingToResponse_thenMapAllFields() {
        val response = mapper.toResponse(OrdemServicoFixtures.itemPeca())

        assertEquals(TipoItemOrcamento.PECA, response.tipo)
        assertEquals(OrdemServicoFixtures.PECA_ID, response.referenciaId.toString())
        assertEquals(OrdemServicoFixtures.PECA_CODIGO, response.codigoReferencia)
        assertEquals(OrdemServicoFixtures.PECA_NOME, response.descricao)
        assertEquals(OrdemServicoFixtures.PECA_PRECO, response.valorUnitario)
        assertEquals(OrdemServicoFixtures.QUANTIDADE_PECA, response.quantidade)
        assertEquals(BigDecimal("179.80"), response.subtotal)
    }

    @Test
    @DisplayName("Dado um item de serviço, quando converter para response, então deve manter o código nulo")
    fun givenServicoItem_whenConvertingToResponse_thenKeepNullCode() {
        val response = mapper.toResponse(OrdemServicoFixtures.itemServico())

        assertEquals(TipoItemOrcamento.SERVICO, response.tipo)
        assertNull(response.codigoReferencia)
        assertEquals(OrdemServicoFixtures.SERVICO_VALOR, response.subtotal)
    }
}
