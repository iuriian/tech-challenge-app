package br.com.fiap.oficina.cliente.application.mapper

import br.com.fiap.oficina.cliente.ClienteFixtures
import br.com.fiap.oficina.cliente.domain.TipoPessoa
import br.com.fiap.oficina.cliente.domain.tipoPessoa
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

@DisplayName("Mapper - Cliente")
class ClienteMapperTest {
    private val mapper = ClienteMapper(EnderecoMapper(), ContatoMapper())

    @Test
    @DisplayName("Dado request de pessoa física, quando converter para domínio, então deve criar um novo cliente")
    fun givenPessoaFisicaRequest_whenConvertingToDomain_thenCreateNewCliente() {
        val cliente = mapper.toDomain(ClienteFixtures.clienteRequest())

        assertNotNull(cliente.id.value)
        assertEquals(ClienteFixtures.NOME, cliente.nome)
        assertEquals(ClienteFixtures.CPF, cliente.documento.valor)
        assertEquals(ClienteFixtures.EMAIL, cliente.email)
        assertEquals("Rua das Flores", cliente.endereco?.logradouro)
        assertEquals(1, cliente.contatos.size)
        assertEquals("CELULAR", cliente.contatos.first().tipo)
    }

    @Test
    @DisplayName("Dado request de pessoa jurídica, quando converter para domínio, então deve usar o CNPJ")
    fun givenPessoaJuridicaRequest_whenConvertingToDomain_thenUseCnpj() {
        val request =
            ClienteFixtures.clienteRequest(
                numeroDocumento = ClienteFixtures.CNPJ,
                tipoPessoa = TipoPessoa.PESSOA_JURIDICA.name,
            )

        val cliente = mapper.toDomain(request)

        assertEquals(ClienteFixtures.CNPJ, cliente.documento.valor)
        assertEquals(TipoPessoa.PESSOA_JURIDICA, cliente.documento.tipoPessoa)
    }

    @Test
    @DisplayName("Dado request sem endereço e contatos, quando converter para domínio, então deve ficar vazio")
    fun givenRequestWithoutEnderecoAndContatos_whenConvertingToDomain_thenBecomeEmpty() {
        val request = ClienteFixtures.clienteRequest(endereco = null, contatos = emptyList())

        val cliente = mapper.toDomain(request)

        assertNull(cliente.endereco)
        assertTrue(cliente.contatos.isEmpty())
    }

    @Test
    @DisplayName("Dado request com tipo de pessoa inválido, quando converter para domínio, então deve lançar exceção")
    fun givenRequestWithInvalidTipoPessoa_whenConvertingToDomain_thenThrowException() {
        val request = ClienteFixtures.clienteRequest(tipoPessoa = "PESSOA_INEXISTENTE")

        assertFailsWith<IllegalArgumentException> { mapper.toDomain(request) }
    }

    @Test
    @DisplayName("Dado request com documento inválido, quando converter para domínio, então deve lançar exceção")
    fun givenRequestWithInvalidDocumento_whenConvertingToDomain_thenThrowException() {
        val request = ClienteFixtures.clienteRequest(numeroDocumento = "12345678900")

        val exception = assertFailsWith<IllegalArgumentException> { mapper.toDomain(request) }

        assertEquals("CPF inválido: 12345678900", exception.message)
    }

    @Test
    @DisplayName("Dado um cliente pessoa física, quando converter para response, então deve formatar o documento")
    fun givenPessoaFisicaCliente_whenConvertingToResponse_thenFormatDocumento() {
        val cliente = ClienteFixtures.cliente()

        val response = mapper.toResponse(cliente)

        assertEquals(ClienteFixtures.ID_1, response.id)
        assertEquals(ClienteFixtures.NOME, response.nome)
        assertEquals("012.345.678-90", response.numeroDocumento)
        assertEquals(TipoPessoa.PESSOA_FISICA.name, response.tipoPessoa)
        assertEquals(ClienteFixtures.EMAIL, response.email)
        assertEquals("Rua das Flores", response.endereco?.logradouro)
        assertEquals(1, response.contatos.size)
    }

    @Test
    @DisplayName("Dado um cliente pessoa jurídica, quando converter para response, então deve formatar o CNPJ")
    fun givenPessoaJuridicaCliente_whenConvertingToResponse_thenFormatCnpj() {
        val cliente = ClienteFixtures.cliente(documento = ClienteFixtures.CNPJ)

        val response = mapper.toResponse(cliente)

        assertEquals("11.222.333/0001-81", response.numeroDocumento)
        assertEquals(TipoPessoa.PESSOA_JURIDICA.name, response.tipoPessoa)
    }

    @Test
    @DisplayName("Dado um cliente sem endereço e contatos, quando converter para response, então deve ficar vazio")
    fun givenClienteWithoutEnderecoAndContatos_whenConvertingToResponse_thenBecomeEmpty() {
        val cliente = ClienteFixtures.cliente(endereco = null, contatos = emptyList())

        val response = mapper.toResponse(cliente)

        assertNull(response.endereco)
        assertTrue(response.contatos.isEmpty())
    }

    @Test
    @DisplayName("Dado um request válido, quando converter ida e volta, então deve preservar os dados")
    fun givenValidRequest_whenConvertingRoundTrip_thenPreserveData() {
        val request = ClienteFixtures.clienteRequest()

        val response = mapper.toResponse(mapper.toDomain(request))

        assertEquals(request.nome, response.nome)
        assertEquals(request.email, response.email)
        assertEquals(request.tipoPessoa, response.tipoPessoa)
        assertEquals(request.endereco?.cep, response.endereco?.cep)
        assertEquals(request.contatos.first().telefone, response.contatos.first().telefone)
    }
}
