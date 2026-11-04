package br.com.fiap.oficina.cliente.application.usecase

import br.com.fiap.oficina.cliente.ClienteFixtures
import br.com.fiap.oficina.cliente.application.mapper.ClienteMapper
import br.com.fiap.oficina.cliente.application.mapper.ContatoMapper
import br.com.fiap.oficina.cliente.application.mapper.EnderecoMapper
import br.com.fiap.oficina.cliente.domain.ClienteRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Use case - Listar Clientes")
class ListarClientesUseCaseTest {
    private val repositoryMock = mockk<ClienteRepository>(relaxed = true)
    private val mapper = ClienteMapper(EnderecoMapper(), ContatoMapper())
    private val usecase = ListarClientesUseCaseImpl(repositoryMock, mapper)

    @Test
    @DisplayName("Dado clientes cadastrados, quando listar, então deve retornar todos convertidos")
    fun givenRegisteredClientes_whenListing_thenReturnAllConverted() {
        val clientes =
            listOf(
                ClienteFixtures.cliente(),
                ClienteFixtures.cliente(
                    id = ClienteFixtures.ID_2,
                    nome = "Maria Souza",
                    documento = ClienteFixtures.CNPJ,
                ),
            )

        every { repositoryMock.listarTodos() } returns clientes

        val resultado = usecase.executar()

        assertEquals(2, resultado.size)
        assertEquals(ClienteFixtures.ID_1, resultado.first().id)
        assertEquals("Maria Souza", resultado.last().nome)
        assertEquals("11.222.333/0001-81", resultado.last().numeroDocumento)

        verify(exactly = 1) { repositoryMock.listarTodos() }
    }

    @Test
    @DisplayName("Dado nenhum cliente cadastrado, quando listar, então deve retornar lista vazia")
    fun givenNoClientes_whenListing_thenReturnEmptyList() {
        every { repositoryMock.listarTodos() } returns emptyList()

        assertTrue(usecase.executar().isEmpty())

        verify(exactly = 1) { repositoryMock.listarTodos() }
    }
}
