package br.com.fiap.oficina.cliente.application.usecase

import br.com.fiap.oficina.cliente.ClienteFixtures
import br.com.fiap.oficina.cliente.application.mapper.ClienteMapper
import br.com.fiap.oficina.cliente.application.mapper.ContatoMapper
import br.com.fiap.oficina.cliente.application.mapper.EnderecoMapper
import br.com.fiap.oficina.cliente.domain.Cliente
import br.com.fiap.oficina.cliente.domain.ClienteRepository
import br.com.fiap.oficina.cliente.domain.TipoPessoa
import br.com.fiap.oficina.shared.domain.exception.BusinessRuleException
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

@DisplayName("Use case - Criar Cliente")
class CriarClienteUseCaseTest {
    private val repositoryMock = mockk<ClienteRepository>(relaxed = true)
    private val mapper = ClienteMapper(EnderecoMapper(), ContatoMapper())
    private val usecase = CriarClienteUseCaseImpl(repositoryMock, mapper)

    @Test
    @DisplayName("Dado dados válidos, quando criar cliente, então deve persistir e retornar o cliente criado")
    fun givenValidData_whenCreatingCliente_thenPersistAndReturnCreatedCliente() {
        val salvo = slot<Cliente>()

        every { repositoryMock.buscarPorDocumento(any()) } returns null
        every { repositoryMock.salvar(capture(salvo)) } answers { salvo.captured }

        val resultado = usecase.executar(ClienteFixtures.clienteRequest())

        assertNotNull(resultado)
        assertEquals(ClienteFixtures.NOME, resultado.nome)
        assertEquals("012.345.678-90", resultado.numeroDocumento)
        assertEquals(TipoPessoa.PESSOA_FISICA.name, resultado.tipoPessoa)
        assertEquals(ClienteFixtures.CPF, salvo.captured.documento.valor)
        assertEquals(1, salvo.captured.contatos.size)

        verify(exactly = 1) { repositoryMock.buscarPorDocumento(any()) }
        verify(exactly = 1) { repositoryMock.salvar(any()) }
    }

    @Test
    @DisplayName("Dado um cliente pessoa jurídica, quando criar cliente, então deve persistir o CNPJ")
    fun givenPessoaJuridica_whenCreatingCliente_thenPersistCnpj() {
        val salvo = slot<Cliente>()

        every { repositoryMock.buscarPorDocumento(any()) } returns null
        every { repositoryMock.salvar(capture(salvo)) } answers { salvo.captured }

        val request =
            ClienteFixtures.clienteRequest(
                numeroDocumento = ClienteFixtures.CNPJ,
                tipoPessoa = TipoPessoa.PESSOA_JURIDICA.name,
            )

        val resultado = usecase.executar(request)

        assertEquals(ClienteFixtures.CNPJ, salvo.captured.documento.valor)
        assertEquals(TipoPessoa.PESSOA_JURIDICA.name, resultado.tipoPessoa)
    }

    @Test
    @DisplayName("Dado um documento já cadastrado, quando criar cliente, então deve lançar exceção")
    fun givenAlreadyRegisteredDocumento_whenCreatingCliente_thenThrowException() {
        every { repositoryMock.buscarPorDocumento(any()) } returns ClienteFixtures.cliente()

        val exception =
            assertFailsWith<BusinessRuleException> {
                usecase.executar(ClienteFixtures.clienteRequest())
            }

        assertEquals("Cliente já cadastrado", exception.message)

        verify(exactly = 1) { repositoryMock.buscarPorDocumento(any()) }
        verify(exactly = 0) { repositoryMock.salvar(any()) }
    }

    @Test
    @DisplayName("Dado um documento inválido, quando criar cliente, então deve lançar exceção")
    fun givenInvalidDocumento_whenCreatingCliente_thenThrowException() {
        assertFailsWith<IllegalArgumentException> {
            usecase.executar(ClienteFixtures.clienteRequest(numeroDocumento = "123"))
        }

        verify(exactly = 0) { repositoryMock.buscarPorDocumento(any()) }
        verify(exactly = 0) { repositoryMock.salvar(any()) }
    }
}
