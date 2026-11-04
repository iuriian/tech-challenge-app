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

@DisplayName("Use case - Listar Serviços Ativos")
class ListarServicosAtivosUseCaseTest {
    private val repositoryMock = mockk<ServicoRepository>()
    private val usecase = ListarServicosAtivosUseCaseImpl(repositoryMock, ServicoMapper())

    @Test
    @DisplayName("Dado serviços ativos, quando listar, então deve retornar os responses correspondentes")
    fun givenActiveServicos_whenListing_thenReturnMatchingResponses() {
        every { repositoryMock.listarAtivos() } returns
            listOf(
                ServicoFixtures.servico(id = ServicoFixtures.ID_1),
                ServicoFixtures.servico(
                    id = ServicoFixtures.ID_2,
                    descricao = ServicoFixtures.DESCRICAO_ALTERNATIVA,
                ),
            )

        val responses = usecase.executar()

        assertEquals(2, responses.size)
        assertEquals(listOf(ServicoFixtures.ID_1, ServicoFixtures.ID_2), responses.map { it.id })
        assertTrue(responses.all { it.ativo })

        verify(exactly = 1) { repositoryMock.listarAtivos() }
        verify(exactly = 0) { repositoryMock.listarTodos() }
    }

    @Test
    @DisplayName("Dado nenhum serviço ativo, quando listar, então deve retornar lista vazia")
    fun givenNoActiveServico_whenListing_thenReturnEmptyList() {
        every { repositoryMock.listarAtivos() } returns emptyList()

        assertTrue(usecase.executar().isEmpty())

        verify(exactly = 1) { repositoryMock.listarAtivos() }
    }
}
