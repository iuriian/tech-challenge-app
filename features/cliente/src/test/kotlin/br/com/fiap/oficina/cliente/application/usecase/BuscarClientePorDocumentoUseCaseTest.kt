package br.com.fiap.oficina.cliente.application.usecase

import br.com.fiap.oficina.cliente.ClienteFixtures
import br.com.fiap.oficina.cliente.application.mapper.ClienteMapper
import br.com.fiap.oficina.cliente.application.mapper.ContatoMapper
import br.com.fiap.oficina.cliente.application.mapper.EnderecoMapper
import br.com.fiap.oficina.cliente.domain.ClienteRepository
import br.com.fiap.oficina.cliente.domain.TipoPessoa
import br.com.fiap.oficina.shared.domain.exception.EntityNotFoundException
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

@DisplayName("Use case - Buscar Cliente por Documento")
class BuscarClientePorDocumentoUseCaseTest {
    private val repositoryMock = mockk<ClienteRepository>(relaxed = true)
    private val mapper = ClienteMapper(EnderecoMapper(), ContatoMapper())
    private val usecase = BuscarClientePorDocumentoUseCaseImpl(repositoryMock, mapper)

    @Test
    @DisplayName("Dado um CPF cadastrado, quando buscar cliente, então deve retornar o cliente")
    fun givenRegisteredCpf_whenSearchingCliente_thenReturnCliente() {
        every { repositoryMock.buscarPorDocumento(any()) } returns ClienteFixtures.cliente()

        val resultado = usecase.executar(ClienteFixtures.CPF)

        assertEquals(ClienteFixtures.ID_1, resultado.id)
        assertEquals("012.345.678-90", resultado.numeroDocumento)
        assertEquals(TipoPessoa.PESSOA_FISICA.name, resultado.tipoPessoa)

        verify(exactly = 1) { repositoryMock.buscarPorDocumento(any()) }
    }

    @Test
    @DisplayName("Dado um CNPJ cadastrado, quando buscar cliente, então deve retornar o cliente")
    fun givenRegisteredCnpj_whenSearchingCliente_thenReturnCliente() {
        every { repositoryMock.buscarPorDocumento(any()) } returns
            ClienteFixtures.cliente(documento = ClienteFixtures.CNPJ)

        val resultado = usecase.executar(ClienteFixtures.CNPJ)

        assertEquals("11.222.333/0001-81", resultado.numeroDocumento)
        assertEquals(TipoPessoa.PESSOA_JURIDICA.name, resultado.tipoPessoa)
    }

    @Test
    @DisplayName("Dado um documento não cadastrado, quando buscar cliente, então deve lançar exceção")
    fun givenUnregisteredDocumento_whenSearchingCliente_thenThrowException() {
        every { repositoryMock.buscarPorDocumento(any()) } returns null

        val exception =
            assertFailsWith<EntityNotFoundException> {
                usecase.executar(ClienteFixtures.CPF)
            }

        assertEquals("Cliente não encontrado, documento: ${ClienteFixtures.CPF}", exception.message)
    }

    @Test
    @DisplayName("Dado um documento inválido, quando buscar cliente, então deve lançar exceção")
    fun givenInvalidDocumento_whenSearchingCliente_thenThrowException() {
        val exception = assertFailsWith<IllegalArgumentException> { usecase.executar("123") }

        assertEquals("Documento inválido: 123", exception.message)

        verify(exactly = 0) { repositoryMock.buscarPorDocumento(any()) }
    }
}
