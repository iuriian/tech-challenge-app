package br.com.fiap.oficina.cliente.application.usecase

import br.com.fiap.oficina.cliente.ClienteFixtures
import br.com.fiap.oficina.cliente.application.mapper.ClienteMapper
import br.com.fiap.oficina.cliente.application.mapper.ContatoMapper
import br.com.fiap.oficina.cliente.application.mapper.EnderecoMapper
import br.com.fiap.oficina.cliente.domain.Cliente
import br.com.fiap.oficina.cliente.domain.ClienteRepository
import br.com.fiap.oficina.shared.domain.exception.BusinessRuleException
import br.com.fiap.oficina.shared.domain.exception.EntityNotFoundException
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

@DisplayName("Use case - Atualizar Cliente")
class AtualizarClienteUseCaseTest {
    private val repositoryMock = mockk<ClienteRepository>(relaxed = true)
    private val mapper = ClienteMapper(EnderecoMapper(), ContatoMapper())
    private val usecase = AtualizarClienteUseCaseImpl(repositoryMock, mapper)

    @Test
    @DisplayName("Dado dados válidos, quando atualizar cliente, então deve retornar o cliente atualizado")
    fun givenValidData_whenUpdatingCliente_thenReturnUpdatedCliente() {
        val cliente = ClienteFixtures.cliente()
        val salvo = slot<Cliente>()

        every { repositoryMock.buscarPorId(any()) } returns cliente
        every { repositoryMock.buscarPorDocumento(any()) } returns null
        every { repositoryMock.salvar(capture(salvo)) } answers { salvo.captured }

        val request =
            ClienteFixtures.clienteRequest(
                nome = "Maria Souza",
                numeroDocumento = ClienteFixtures.CPF_OUTRO,
                email = "maria@email.com",
            )

        val resultado = usecase.executar(ClienteFixtures.ID_1, request)

        assertNotNull(resultado)
        assertEquals(cliente.id, salvo.captured.id)
        assertEquals("Maria Souza", salvo.captured.nome)
        assertEquals(ClienteFixtures.CPF_OUTRO, salvo.captured.documento.valor)
        assertEquals("maria@email.com", salvo.captured.email)
        assertEquals(ClienteFixtures.ID_1, resultado.id)

        verify(exactly = 1) { repositoryMock.buscarPorId(any()) }
        verify(exactly = 1) { repositoryMock.buscarPorDocumento(any()) }
        verify(exactly = 1) { repositoryMock.salvar(any()) }
    }

    @Test
    @DisplayName("Dado documento inalterado, quando atualizar cliente, então não deve consultar o documento")
    fun givenUnchangedDocumento_whenUpdatingCliente_thenNotSearchByDocumento() {
        val cliente = ClienteFixtures.cliente()
        val salvo = slot<Cliente>()

        every { repositoryMock.buscarPorId(any()) } returns cliente
        every { repositoryMock.salvar(capture(salvo)) } answers { salvo.captured }

        val request = ClienteFixtures.clienteRequest(nome = "João Silva Júnior")

        val resultado = usecase.executar(ClienteFixtures.ID_1, request)

        assertNotNull(resultado)
        assertEquals("João Silva Júnior", salvo.captured.nome)
        assertEquals(ClienteFixtures.CPF, salvo.captured.documento.valor)

        verify(exactly = 1) { repositoryMock.buscarPorId(any()) }
        verify(exactly = 0) { repositoryMock.buscarPorDocumento(any()) }
        verify(exactly = 1) { repositoryMock.salvar(any()) }
    }

    @Test
    @DisplayName("Dado documento pertencente ao próprio cliente, quando atualizar cliente, então deve salvar")
    fun givenDocumentoOwnedBySameCliente_whenUpdatingCliente_thenSave() {
        val cliente = ClienteFixtures.cliente()
        val mesmoCliente = ClienteFixtures.cliente(documento = ClienteFixtures.CPF_OUTRO)

        every { repositoryMock.buscarPorId(any()) } returns cliente
        every { repositoryMock.buscarPorDocumento(any()) } returns mesmoCliente
        every { repositoryMock.salvar(any()) } answers { firstArg() }

        val request = ClienteFixtures.clienteRequest(numeroDocumento = ClienteFixtures.CPF_OUTRO)

        assertNotNull(usecase.executar(ClienteFixtures.ID_1, request))

        verify(exactly = 1) { repositoryMock.buscarPorDocumento(any()) }
        verify(exactly = 1) { repositoryMock.salvar(any()) }
    }

    @Test
    @DisplayName("Dado endereço e contatos nulos, quando atualizar cliente, então deve limpar os dados")
    fun givenNullEnderecoAndContatos_whenUpdatingCliente_thenClearData() {
        val salvo = slot<Cliente>()

        every { repositoryMock.buscarPorId(any()) } returns ClienteFixtures.cliente()
        every { repositoryMock.salvar(capture(salvo)) } answers { salvo.captured }

        val request = ClienteFixtures.clienteRequest(endereco = null, contatos = emptyList())

        val resultado = usecase.executar(ClienteFixtures.ID_1, request)

        assertNull(salvo.captured.endereco)
        assertNull(resultado.endereco)
        assertEquals(0, resultado.contatos.size)
    }

    @Test
    @DisplayName("Dado id em branco, quando atualizar cliente, então deve lançar exceção")
    fun givenBlankId_whenUpdatingCliente_thenThrowException() {
        val exception =
            assertFailsWith<IllegalArgumentException> {
                usecase.executar("", ClienteFixtures.clienteRequest())
            }

        assertEquals("Id é obrigatório!", exception.message)

        verify(exactly = 0) { repositoryMock.buscarPorId(any()) }
        verify(exactly = 0) { repositoryMock.salvar(any()) }
    }

    @Test
    @DisplayName("Dado id em formato inválido, quando atualizar cliente, então deve lançar exceção")
    fun givenMalformedId_whenUpdatingCliente_thenThrowException() {
        assertFailsWith<IllegalArgumentException> {
            usecase.executar("nao-e-um-uuid", ClienteFixtures.clienteRequest())
        }

        verify(exactly = 0) { repositoryMock.buscarPorId(any()) }
        verify(exactly = 0) { repositoryMock.salvar(any()) }
    }

    @Test
    @DisplayName("Dado id inexistente, quando atualizar cliente, então deve lançar exceção")
    fun givenNonExistentId_whenUpdatingCliente_thenThrowException() {
        every { repositoryMock.buscarPorId(any()) } returns null

        val exception =
            assertFailsWith<EntityNotFoundException> {
                usecase.executar(ClienteFixtures.ID_1, ClienteFixtures.clienteRequest())
            }

        assertEquals("Cliente não encontrado!", exception.message)

        verify(exactly = 1) { repositoryMock.buscarPorId(any()) }
        verify(exactly = 0) { repositoryMock.salvar(any()) }
    }

    @Test
    @DisplayName("Dado documento de outro cliente, quando atualizar cliente, então deve lançar exceção")
    fun givenDocumentoFromAnotherCliente_whenUpdatingCliente_thenThrowException() {
        val outroCliente = ClienteFixtures.cliente(id = ClienteFixtures.ID_2, documento = ClienteFixtures.CPF_OUTRO)

        every { repositoryMock.buscarPorId(any()) } returns ClienteFixtures.cliente()
        every { repositoryMock.buscarPorDocumento(any()) } returns outroCliente

        val request = ClienteFixtures.clienteRequest(numeroDocumento = ClienteFixtures.CPF_OUTRO)

        val exception =
            assertFailsWith<BusinessRuleException> {
                usecase.executar(ClienteFixtures.ID_1, request)
            }

        assertEquals("Número de documento já cadastrado!", exception.message)

        verify(exactly = 1) { repositoryMock.buscarPorDocumento(any()) }
        verify(exactly = 0) { repositoryMock.salvar(any()) }
    }
}
