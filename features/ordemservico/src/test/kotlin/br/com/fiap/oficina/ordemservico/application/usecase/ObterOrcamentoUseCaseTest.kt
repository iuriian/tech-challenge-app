package br.com.fiap.oficina.ordemservico.application.usecase

import br.com.fiap.oficina.ordemservico.OrdemServicoFixtures
import br.com.fiap.oficina.ordemservico.application.mapper.ItemOrcamentoMapper
import br.com.fiap.oficina.ordemservico.application.mapper.OrcamentoMapper
import br.com.fiap.oficina.ordemservico.domain.OrdemServicoId
import br.com.fiap.oficina.ordemservico.domain.OrdemServicoRepository
import br.com.fiap.oficina.shared.domain.exception.EntityNotFoundException
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import kotlin.test.assertFailsWith

@DisplayName("Use case - Obter Orçamento")
class ObterOrcamentoUseCaseTest {
    private val repositoryMock = mockk<OrdemServicoRepository>()
    private val usecase = ObterOrcamentoUseCaseImpl(repositoryMock, OrcamentoMapper(ItemOrcamentoMapper()))

    @Test
    @DisplayName("Dado uma ordem existente, quando obter o orçamento, então deve retornar os itens e o total")
    fun givenExistingOrdem_whenGettingBudget_thenReturnItemsAndTotal() {
        val id = OrdemServicoId.toUUID(OrdemServicoFixtures.ID)

        every { repositoryMock.buscarPorId(id) } returns OrdemServicoFixtures.ordemServico()

        val response = usecase.executar(OrdemServicoFixtures.ID)

        assertEquals(2, response.itens.size)
        assertEquals(BigDecimal("329.80"), response.valorTotal)

        verify(exactly = 1) { repositoryMock.buscarPorId(id) }
    }

    @Test
    @DisplayName("Dado uma ordem sem itens, quando obter o orçamento, então deve retornar total zerado")
    fun givenOrdemWithoutItems_whenGettingBudget_thenReturnZeroTotal() {
        every { repositoryMock.buscarPorId(any()) } returns
            OrdemServicoFixtures.ordemServico(orcamento = OrdemServicoFixtures.orcamento(emptyList()))

        val response = usecase.executar(OrdemServicoFixtures.ID)

        assertTrue(response.itens.isEmpty())
        assertEquals(BigDecimal.ZERO, response.valorTotal)
    }

    @Test
    @DisplayName("Dado uma ordem inexistente, quando obter o orçamento, então deve lançar exceção")
    fun givenNonExistentOrdem_whenGettingBudget_thenThrowException() {
        every { repositoryMock.buscarPorId(any()) } returns null

        val exception = assertFailsWith<EntityNotFoundException> { usecase.executar(OrdemServicoFixtures.ID) }

        assertEquals("Ordem de serviço não encontrada, id: ${OrdemServicoFixtures.ID}", exception.message)
    }

    @Test
    @DisplayName("Dado um id inválido, quando obter o orçamento, então deve lançar exceção")
    fun givenInvalidId_whenGettingBudget_thenThrowException() {
        assertFailsWith<IllegalArgumentException> { usecase.executar(OrdemServicoFixtures.ID_INVALIDO) }

        verify(exactly = 0) { repositoryMock.buscarPorId(any()) }
    }
}
