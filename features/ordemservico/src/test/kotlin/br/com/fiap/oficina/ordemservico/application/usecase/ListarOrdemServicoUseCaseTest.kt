package br.com.fiap.oficina.ordemservico.application.usecase

import br.com.fiap.oficina.ordemservico.OrdemServicoFixtures
import br.com.fiap.oficina.ordemservico.application.mapper.ItemOrcamentoMapper
import br.com.fiap.oficina.ordemservico.application.mapper.OrdemServicoMapper
import br.com.fiap.oficina.ordemservico.domain.OrdemServicoRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Use case - Listar Ordens de Serviço")
class ListarOrdemServicoUseCaseTest {
    private val repositoryMock = mockk<OrdemServicoRepository>()
    private val usecase = ListarOrdemServicoUseCaseImpl(repositoryMock, OrdemServicoMapper(ItemOrcamentoMapper()))

    @Test
    @DisplayName("Dado ordens cadastradas, quando listar, então deve retornar todos os responses")
    fun givenRegisteredOrdens_whenListing_thenReturnAllResponses() {
        every { repositoryMock.listarTodos() } returns
            listOf(
                OrdemServicoFixtures.ordemServico(),
                OrdemServicoFixtures.ordemServico(id = OrdemServicoFixtures.ID_ALTERNATIVO),
            )

        val responses = usecase.executar()

        assertEquals(2, responses.size)
        assertEquals(OrdemServicoFixtures.ID, responses.first().id)
        assertEquals(OrdemServicoFixtures.ID_ALTERNATIVO, responses.last().id)

        verify(exactly = 1) { repositoryMock.listarTodos() }
    }

    @Test
    @DisplayName("Dado nenhuma ordem cadastrada, quando listar, então deve retornar lista vazia")
    fun givenNoRegisteredOrdens_whenListing_thenReturnEmptyList() {
        every { repositoryMock.listarTodos() } returns emptyList()

        assertTrue(usecase.executar().isEmpty())
    }
}
