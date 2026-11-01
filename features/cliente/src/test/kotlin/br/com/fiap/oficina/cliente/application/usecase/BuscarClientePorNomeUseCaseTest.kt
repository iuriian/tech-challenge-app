package br.com.fiap.oficina.cliente.application.usecase

import br.com.fiap.oficina.cliente.ClienteFixtures
import br.com.fiap.oficina.cliente.application.mapper.ClienteMapper
import br.com.fiap.oficina.cliente.application.mapper.ContatoMapper
import br.com.fiap.oficina.cliente.application.mapper.EnderecoMapper
import br.com.fiap.oficina.cliente.domain.ClienteRepository
import br.com.fiap.oficina.shared.domain.exception.EntityNotFoundException
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

@DisplayName("Use case - Buscar Cliente por Nome")
class BuscarClientePorNomeUseCaseTest {
    private val repositoryMock = mockk<ClienteRepository>(relaxed = true)
    private val mapper = ClienteMapper(EnderecoMapper(), ContatoMapper())
    private val usecase = BuscarClientePorNomeUseCaseImpl(repositoryMock, mapper)

    @Test
    @DisplayName("Dado um nome cadastrado, quando buscar cliente, então deve retornar o cliente")
    fun givenRegisteredNome_whenSearchingCliente_thenReturnCliente() {
        every { repositoryMock.buscarPorNome(ClienteFixtures.NOME) } returns ClienteFixtures.cliente()

        val resultado = usecase.executar(ClienteFixtures.NOME)

        assertEquals(ClienteFixtures.ID_1, resultado.id)
        assertEquals(ClienteFixtures.NOME, resultado.nome)

        verify(exactly = 1) { repositoryMock.buscarPorNome(ClienteFixtures.NOME) }
    }

    @Test
    @DisplayName("Dado um nome não cadastrado, quando buscar cliente, então deve lançar exceção")
    fun givenUnregisteredNome_whenSearchingCliente_thenThrowException() {
        every { repositoryMock.buscarPorNome(any()) } returns null

        val exception =
            assertFailsWith<EntityNotFoundException> {
                usecase.executar("Inexistente")
            }

        assertEquals("Cliente não encontrado com o nome: Inexistente", exception.message)

        verify(exactly = 1) { repositoryMock.buscarPorNome("Inexistente") }
    }
}
