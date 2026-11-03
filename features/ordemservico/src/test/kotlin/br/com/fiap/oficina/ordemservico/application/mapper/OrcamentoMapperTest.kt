package br.com.fiap.oficina.ordemservico.application.mapper

import br.com.fiap.oficina.ordemservico.OrdemServicoFixtures
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.math.BigDecimal

@DisplayName("Aplicação - Mapper de Orçamento")
class OrcamentoMapperTest {
    private val mapper = OrcamentoMapper(ItemOrcamentoMapper())

    @Test
    @DisplayName("Dado um orçamento com itens, quando converter, então deve mapear os itens e o valor total")
    fun givenBudgetWithItems_whenConverting_thenMapItemsAndTotal() {
        val response = mapper.toResponse(OrdemServicoFixtures.orcamento())

        assertEquals(2, response.itens.size)
        assertEquals(BigDecimal("329.80"), response.valorTotal)
    }

    @Test
    @DisplayName("Dado um orçamento vazio, quando converter, então deve retornar total zerado")
    fun givenEmptyBudget_whenConverting_thenReturnZeroTotal() {
        val response = mapper.toResponse(OrdemServicoFixtures.orcamento(emptyList()))

        assertTrue(response.itens.isEmpty())
        assertEquals(BigDecimal.ZERO, response.valorTotal)
    }
}
