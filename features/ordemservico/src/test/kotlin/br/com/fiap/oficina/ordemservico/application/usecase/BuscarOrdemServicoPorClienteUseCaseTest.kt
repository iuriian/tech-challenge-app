package br.com.fiap.oficina.ordemservico.application.usecase

import br.com.fiap.oficina.ordemservico.OrdemServicoFixtures
import br.com.fiap.oficina.ordemservico.application.mapper.ItemOrcamentoMapper
import br.com.fiap.oficina.ordemservico.application.mapper.OrdemServicoMapper
import br.com.fiap.oficina.ordemservico.domain.ClienteRef
import br.com.fiap.oficina.ordemservico.domain.OrdemServicoRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

@DisplayName("Use case - Buscar Ordens de Serviço por cliente")
class BuscarOrdemServicoPorClienteUseCaseTest {
    private val repositoryMock = mockk<OrdemServicoRepository>()
    private val usecase =
        BuscarOrdemServicoPorClienteUseCaseImpl(repositoryMock, OrdemServicoMapper(ItemOrcamentoMapper()))

    @Test
    @DisplayName("Dado um cliente com ordens, quando buscar, então deve retornar os responses do cliente")
    fun givenClienteWithOrdens_whenSearching_thenReturnClienteResponses() {
        val ref = ClienteRef.toUUID(OrdemServicoFixtures.CLIENTE_ID)

        every { repositoryMock.buscarPorCliente(ref) } returns
            listOf(
                OrdemServicoFixtures.ordemServico(),
                OrdemServicoFixtures.ordemServico(id = OrdemServicoFixtures.ID_ALTERNATIVO),
            )

        val responses = usecase.executar(OrdemServicoFixtures.CLIENTE_ID)

        assertEquals(2, responses.size)
        assertEquals(OrdemServicoFixtures.CLIENTE_ID, responses.first().clienteId)

        verify(exactly = 1) { repositoryMock.buscarPorCliente(ref) }
    }

    @Test
    @DisplayName("Dado um cliente sem ordens, quando buscar, então deve retornar lista vazia")
    fun givenClienteWithoutOrdens_whenSearching_thenReturnEmptyList() {
        every { repositoryMock.buscarPorCliente(any()) } returns emptyList()

        assertTrue(usecase.executar(OrdemServicoFixtures.CLIENTE_ID).isEmpty())
    }

    @Test
    @DisplayName("Dado um id de cliente inválido, quando buscar, então deve lançar exceção")
    fun givenInvalidClienteId_whenSearching_thenThrowException() {
        assertFailsWith<IllegalArgumentException> { usecase.executar(OrdemServicoFixtures.ID_INVALIDO) }

        verify(exactly = 0) { repositoryMock.buscarPorCliente(any()) }
    }
}
