package br.com.fiap.oficina.ordemservico.application.resolver

import br.com.fiap.oficina.ordemservico.OrdemServicoFixtures
import br.com.fiap.oficina.ordemservico.domain.TipoItemOrcamento
import br.com.fiap.oficina.ordemservico.domain.port.peca.ConsultarPeca
import br.com.fiap.oficina.ordemservico.domain.port.servico.ConsultarServico
import br.com.fiap.oficina.shared.domain.exception.BusinessRuleException
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import kotlin.test.assertFailsWith

@DisplayName("Aplicação - Resolver de orçamento")
class OrcamentoResolverTest {
    private val consultarPecaMock = mockk<ConsultarPeca>()
    private val consultarServicoMock = mockk<ConsultarServico>()
    private val resolver = OrcamentoResolver(consultarPecaMock, consultarServicoMock)

    @Test
    @DisplayName("Dado itens de peça e serviço, quando resolver, então deve montar o orçamento com o preço praticado")
    fun givenPecaAndServicoItems_whenResolving_thenAssembleBudgetWithChargedPrice() {
        every { consultarPecaMock.buscarPorId(any()) } returns OrdemServicoFixtures.dadosPeca()
        every { consultarServicoMock.buscarPorId(any()) } returns OrdemServicoFixtures.dadosServico()

        val orcamento =
            resolver.resolver(
                listOf(
                    OrdemServicoFixtures.itemRequest(),
                    OrdemServicoFixtures.itemRequest(
                        tipo = TipoItemOrcamento.SERVICO,
                        referenciaId = OrdemServicoFixtures.SERVICO_ID,
                        quantidade = BigDecimal.ONE,
                    ),
                ),
            )

        assertEquals(2, orcamento.itens.size)
        assertEquals(OrdemServicoFixtures.PECA_NOME, orcamento.itens.first().descricao)
        assertEquals(OrdemServicoFixtures.PECA_CODIGO, orcamento.itens.first().codigoReferencia)
        assertEquals(OrdemServicoFixtures.SERVICO_DESCRICAO, orcamento.itens.last().descricao)
        assertEquals(BigDecimal("329.80"), orcamento.valorTotal)
    }

    @Test
    @DisplayName("Dado uma lista vazia, quando resolver, então deve retornar orçamento vazio sem consultar")
    fun givenEmptyList_whenResolving_thenReturnEmptyBudgetWithoutQuerying() {
        val orcamento = resolver.resolver(emptyList())

        assertTrue(orcamento.itens.isEmpty())
        assertEquals(BigDecimal.ZERO, orcamento.valorTotal)

        verify(exactly = 0) { consultarPecaMock.buscarPorId(any()) }
        verify(exactly = 0) { consultarServicoMock.buscarPorId(any()) }
    }

    @Test
    @DisplayName("Dado uma peça inexistente, quando resolver, então deve lançar exceção de regra de negócio")
    fun givenNonExistentPeca_whenResolving_thenThrowBusinessRuleException() {
        every { consultarPecaMock.buscarPorId(any()) } returns null

        val exception =
            assertFailsWith<BusinessRuleException> {
                resolver.resolver(listOf(OrdemServicoFixtures.itemRequest()))
            }

        assertEquals("Peça não encontrada com o id: ${OrdemServicoFixtures.PECA_ID}", exception.message)
    }

    @Test
    @DisplayName("Dado uma peça inativa, quando resolver, então deve lançar exceção de regra de negócio")
    fun givenInactivePeca_whenResolving_thenThrowBusinessRuleException() {
        every { consultarPecaMock.buscarPorId(any()) } returns OrdemServicoFixtures.dadosPeca(ativo = false)

        val exception =
            assertFailsWith<BusinessRuleException> {
                resolver.resolver(listOf(OrdemServicoFixtures.itemRequest()))
            }

        assertEquals(
            "Peça inativa não pode compor o orçamento, id: ${OrdemServicoFixtures.PECA_ID}",
            exception.message,
        )
    }

    @Test
    @DisplayName("Dado um serviço inexistente, quando resolver, então deve lançar exceção de regra de negócio")
    fun givenNonExistentServico_whenResolving_thenThrowBusinessRuleException() {
        every { consultarServicoMock.buscarPorId(any()) } returns null

        val exception =
            assertFailsWith<BusinessRuleException> {
                resolver.resolver(listOf(itemServicoRequest()))
            }

        assertEquals("Serviço não encontrado com o id: ${OrdemServicoFixtures.SERVICO_ID}", exception.message)
    }

    @Test
    @DisplayName("Dado um serviço inativo, quando resolver, então deve lançar exceção de regra de negócio")
    fun givenInactiveServico_whenResolving_thenThrowBusinessRuleException() {
        every { consultarServicoMock.buscarPorId(any()) } returns OrdemServicoFixtures.dadosServico(ativo = false)

        val exception =
            assertFailsWith<BusinessRuleException> {
                resolver.resolver(listOf(itemServicoRequest()))
            }

        assertEquals(
            "Serviço inativo não pode compor o orçamento, id: ${OrdemServicoFixtures.SERVICO_ID}",
            exception.message,
        )
    }

    @Test
    @DisplayName("Dado um id de referência inválido, quando resolver, então deve lançar exceção sem consultar")
    fun givenInvalidReferenceId_whenResolving_thenThrowExceptionWithoutQuerying() {
        assertFailsWith<IllegalArgumentException> {
            resolver.resolver(
                listOf(OrdemServicoFixtures.itemRequest(referenciaId = OrdemServicoFixtures.ID_INVALIDO)),
            )
        }

        verify(exactly = 0) { consultarPecaMock.buscarPorId(any()) }
    }

    private fun itemServicoRequest() = OrdemServicoFixtures.itemRequest(
        tipo = TipoItemOrcamento.SERVICO,
        referenciaId = OrdemServicoFixtures.SERVICO_ID,
        quantidade = BigDecimal.ONE,
    )
}
