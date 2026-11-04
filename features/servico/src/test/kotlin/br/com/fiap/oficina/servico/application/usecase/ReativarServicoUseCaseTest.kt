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
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

@DisplayName("Use case - Reativar Serviço")
class ReativarServicoUseCaseTest {
    private val repositoryMock = mockk<ServicoRepository>()
    private val usecase = ReativarServicoUseCaseImpl(repositoryMock, ServicoMapper())

    @Test
    @DisplayName("Dado um serviço inativo, quando reativar, então deve retornar o response ativo")
    fun givenInactiveServico_whenReactivating_thenReturnActiveResponse() {
        val id = ServicoId.toUUID(ServicoFixtures.ID_1)

        every { repositoryMock.buscarPorId(id) } returns ServicoFixtures.servico(ativo = false)
        every { repositoryMock.salvar(any()) } answers { firstArg() }

        val response = usecase.executar(ServicoFixtures.ID_1)

        assertTrue(response.ativo)
        assertEquals(ServicoFixtures.ID_1, response.id)

        verify(exactly = 1) { repositoryMock.salvar(any()) }
    }

    @Test
    @DisplayName("Dado um id inexistente, quando reativar, então deve lançar exceção e não persistir")
    fun givenNonExistentId_whenReactivating_thenThrowExceptionAndDoNotPersist() {
        every { repositoryMock.buscarPorId(any()) } returns null

        val exception =
            assertFailsWith<EntityNotFoundException> {
                usecase.executar(ServicoFixtures.ID_1)
            }

        assertEquals("Serviço não encontrado com o id: ${ServicoFixtures.ID_1}", exception.message)

        verify(exactly = 0) { repositoryMock.salvar(any()) }
    }

    @Test
    @DisplayName("Dado um id inválido, quando reativar, então deve lançar exceção")
    fun givenInvalidId_whenReactivating_thenThrowException() {
        assertFailsWith<IllegalArgumentException> { usecase.executar("nao-e-um-uuid") }

        verify(exactly = 0) { repositoryMock.buscarPorId(any()) }
    }
}
