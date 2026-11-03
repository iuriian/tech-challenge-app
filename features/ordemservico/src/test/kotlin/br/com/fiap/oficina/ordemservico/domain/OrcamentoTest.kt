package br.com.fiap.oficina.ordemservico.domain

import br.com.fiap.oficina.ordemservico.OrdemServicoFixtures
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.math.BigDecimal

@DisplayName("Domínio - Orçamento")
class OrcamentoTest {
    @Test
    @DisplayName("Dado um orçamento sem itens, quando consultar o valor total, então deve retornar zero")
    fun givenBudgetWithoutItems_whenQueryingTotal_thenReturnZero() {
        val orcamento = Orcamento()

        assertTrue(orcamento.itens.isEmpty())
        assertEquals(BigDecimal.ZERO, orcamento.valorTotal)
    }

    @Test
    @DisplayName("Dado um orçamento com itens, quando consultar o valor total, então deve somar os subtotais")
    fun givenBudgetWithItems_whenQueryingTotal_thenSumSubtotals() {
        val orcamento = OrdemServicoFixtures.orcamento()

        assertEquals(2, orcamento.itens.size)
        assertEquals(BigDecimal("329.80"), orcamento.valorTotal)
    }

    @Test
    @DisplayName("Dado uma lista de itens, quando criar pela fábrica, então deve manter os itens")
    fun givenItemList_whenCreatingByFactory_thenKeepItems() {
        val itens = listOf(OrdemServicoFixtures.itemPeca())

        assertEquals(itens, Orcamento.criar(itens).itens)
    }

    @Test
    @DisplayName("Dado a fábrica sem argumentos, quando criar, então deve retornar orçamento vazio")
    fun givenFactoryWithoutArguments_whenCreating_thenReturnEmptyBudget() {
        assertTrue(Orcamento.criar().itens.isEmpty())
    }

    @Test
    @DisplayName("Dado um orçamento, quando adicionar item, então deve retornar nova instância sem alterar a original")
    fun givenBudget_whenAddingItem_thenReturnNewInstanceWithoutMutatingOriginal() {
        val original = Orcamento()

        val comItem = original.adicionarItem(OrdemServicoFixtures.itemServico())

        assertEquals(1, comItem.itens.size)
        assertEquals(OrdemServicoFixtures.SERVICO_VALOR, comItem.valorTotal)
        assertTrue(original.itens.isEmpty())
    }

    @Test
    @DisplayName("Dado os mesmos itens, quando comparar os orçamentos, então devem ser iguais")
    fun givenSameItems_whenComparingBudgets_thenTheyAreEqual() {
        val primeiro = OrdemServicoFixtures.orcamento()
        val segundo = OrdemServicoFixtures.orcamento()

        assertEquals(primeiro, segundo)
        assertEquals(primeiro.hashCode(), segundo.hashCode())
        assertEquals(primeiro, primeiro.copy())
        assertEquals(primeiro.itens, primeiro.component1())
        assertTrue(primeiro.toString().contains(OrdemServicoFixtures.PECA_NOME))
    }
}
