package br.com.fiap.oficina.cliente.application.usecase

import br.com.fiap.oficina.cliente.ClienteFixtures
import br.com.fiap.oficina.cliente.domain.ClienteId
import br.com.fiap.oficina.cliente.domain.ClienteRepository
import br.com.fiap.oficina.shared.domain.exception.EntityNotFoundException
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

@DisplayName("Use case - Remover Cliente")
class RemoverClienteUseCaseTest {
    private val repositoryMock = mockk<ClienteRepository>(relaxed = true)
    private val usecase = RemoverClienteUseCaseImpl(repositoryMock)

    @Test
    @DisplayName("Dado um id existente, quando remover cliente, então deve deletar o cliente")
    fun givenExistingId_whenRemovingCliente_thenDeleteCliente() {
        val cliente = ClienteFixtures.cliente()

        every { repositoryMock.buscarPorId(any()) } returns cliente

        usecase.executar(ClienteFixtures.ID_1)

        verify(exactly = 1) { repositoryMock.buscarPorId(ClienteId.toUUID(ClienteFixtures.ID_1)) }
        verify(exactly = 1) { repositoryMock.deletar(cliente.id) }
    }

    @Test
    @DisplayName("Dado um id inexistente, quando remover cliente, então deve lançar exceção")
    fun givenNonExistentId_whenRemovingCliente_thenThrowException() {
        every { repositoryMock.buscarPorId(any()) } returns null

        val exception =
            assertFailsWith<EntityNotFoundException> {
                usecase.executar(ClienteFixtures.ID_1)
            }

        assertEquals("Cliente não encontrado com o ID: ${ClienteFixtures.ID_1}", exception.message)

        verify(exactly = 0) { repositoryMock.deletar(any()) }
    }

    @Test
    @DisplayName("Dado um id em formato inválido, quando remover cliente, então deve lançar exceção")
    fun givenMalformedId_whenRemovingCliente_thenThrowException() {
        assertFailsWith<IllegalArgumentException> { usecase.executar("nao-e-um-uuid") }

        verify(exactly = 0) { repositoryMock.buscarPorId(any()) }
        verify(exactly = 0) { repositoryMock.deletar(any()) }
    }
}
