package br.com.fiap.oficina.servico.application.usecase

import br.com.fiap.oficina.servico.ServicoFixtures
import br.com.fiap.oficina.servico.application.mapper.ServicoMapper
import br.com.fiap.oficina.servico.domain.ServicoRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Use case - Listar Todos os Serviços")
class ListarTodosServicosUseCaseTest {
    private val repositoryMock = mockk<ServicoRepository>()
    private val usecase = ListarTodosServicosUseCaseImpl(repositoryMock, ServicoMapper())

    @Test
    @DisplayName("Dado serviços ativos e inativos, quando listar, então deve retornar todos os responses")
    fun givenActiveAndInactiveServicos_whenListing_thenReturnAllResponses() {
        every { repositoryMock.listarTodos() } returns
            listOf(
                ServicoFixtures.servico(id = ServicoFixtures.ID_1, ativo = true),
                ServicoFixtures.servico(id = ServicoFixtures.ID_2, ativo = false),
            )

        val responses = usecase.executar()

        assertEquals(2, responses.size)
        assertEquals(listOf(true, false), responses.map { it.ativo })

        verify(exactly = 1) { repositoryMock.listarTodos() }
        verify(exactly = 0) { repositoryMock.listarAtivos() }
    }

    @Test
    @DisplayName("Dado nenhum serviço cadastrado, quando listar, então deve retornar lista vazia")
    fun givenNoServico_whenListing_thenReturnEmptyList() {
        every { repositoryMock.listarTodos() } returns emptyList()

        assertTrue(usecase.executar().isEmpty())

        verify(exactly = 1) { repositoryMock.listarTodos() }
    }
}
