package br.com.fiap.oficina.cliente.application.usecase

import br.com.fiap.oficina.cliente.ClienteFixtures
import br.com.fiap.oficina.cliente.application.mapper.ClienteMapper
import br.com.fiap.oficina.cliente.application.mapper.ContatoMapper
import br.com.fiap.oficina.cliente.application.mapper.EnderecoMapper
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

@DisplayName("Use case - Buscar Cliente por Id")
class BuscarClientePorIdUseCaseTest {
    private val repositoryMock = mockk<ClienteRepository>(relaxed = true)
    private val mapper = ClienteMapper(EnderecoMapper(), ContatoMapper())
    private val usecase = BuscarClientePorIdUseCaseImpl(repositoryMock, mapper)

    @Test
    @DisplayName("Dado um id existente, quando buscar cliente, então deve retornar o cliente")
    fun givenExistingId_whenSearchingCliente_thenReturnCliente() {
        every { repositoryMock.buscarPorId(ClienteId.toUUID(ClienteFixtures.ID_1)) } returns ClienteFixtures.cliente()

        val resultado = usecase.executar(ClienteFixtures.ID_1)

        assertEquals(ClienteFixtures.ID_1, resultado.id)
        assertEquals(ClienteFixtures.NOME, resultado.nome)
        assertEquals("012.345.678-90", resultado.numeroDocumento)

        verify(exactly = 1) { repositoryMock.buscarPorId(ClienteId.toUUID(ClienteFixtures.ID_1)) }
    }

    @Test
    @DisplayName("Dado um id inexistente, quando buscar cliente, então deve lançar exceção")
    fun givenNonExistentId_whenSearchingCliente_thenThrowException() {
        every { repositoryMock.buscarPorId(any()) } returns null

        val exception =
            assertFailsWith<EntityNotFoundException> {
                usecase.executar(ClienteFixtures.ID_1)
            }

        assertEquals("Cliente não encontrado, id: ${ClienteFixtures.ID_1}", exception.message)
    }

    @Test
    @DisplayName("Dado um id em formato inválido, quando buscar cliente, então deve lançar exceção")
    fun givenMalformedId_whenSearchingCliente_thenThrowException() {
        assertFailsWith<IllegalArgumentException> { usecase.executar("nao-e-um-uuid") }

        verify(exactly = 0) { repositoryMock.buscarPorId(any()) }
    }
}
