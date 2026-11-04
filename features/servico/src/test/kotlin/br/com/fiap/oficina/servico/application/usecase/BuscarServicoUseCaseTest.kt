package br.com.fiap.oficina.servico.application.usecase

import br.com.fiap.oficina.servico.ServicoFixtures
import br.com.fiap.oficina.servico.application.mapper.ServicoMapper
import br.com.fiap.oficina.servico.domain.ServicoId
import br.com.fiap.oficina.servico.domain.ServicoRepository
import br.com.fiap.oficina.shared.domain.exception.EntityNotFoundException
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

@DisplayName("Use case - Buscar Serviço")
class BuscarServicoUseCaseTest {
    private val repositoryMock = mockk<ServicoRepository>()
    private val usecase = BuscarServicoUseCaseImpl(repositoryMock, ServicoMapper())

    @Test
    @DisplayName("Dado um id existente, quando buscar serviço, então deve retornar o response")
    fun givenExistingId_whenSearchingServico_thenReturnResponse() {
        val id = ServicoId.toUUID(ServicoFixtures.ID_1)

        every { repositoryMock.buscarPorId(id) } returns ServicoFixtures.servico()

        val response = usecase.executar(ServicoFixtures.ID_1)

        assertEquals(ServicoFixtures.ID_1, response.id)
        assertEquals(ServicoFixtures.DESCRICAO, response.descricao)

        verify(exactly = 1) { repositoryMock.buscarPorId(id) }
    }

    @Test
    @DisplayName("Dado um id inexistente, quando buscar serviço, então deve lançar exceção")
    fun givenNonExistentId_whenSearchingServico_thenThrowException() {
        every { repositoryMock.buscarPorId(any()) } returns null

        val exception =
            assertFailsWith<EntityNotFoundException> {
                usecase.executar(ServicoFixtures.ID_1)
            }

        assertEquals("Serviço não encontrado com o id: ${ServicoFixtures.ID_1}", exception.message)
    }

    @Test
    @DisplayName("Dado um id inválido, quando buscar serviço, então deve lançar exceção")
    fun givenInvalidId_whenSearchingServico_thenThrowException() {
        assertFailsWith<IllegalArgumentException> { usecase.executar("nao-e-um-uuid") }

        verify(exactly = 0) { repositoryMock.buscarPorId(any()) }
    }
}
