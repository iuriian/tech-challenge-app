package br.com.fiap.oficina.cliente.application.mapper

import br.com.fiap.oficina.cliente.ClienteFixtures
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

@DisplayName("Mapper - Endereço")
class EnderecoMapperTest {
    private val mapper = EnderecoMapper()

    @Test
    @DisplayName("Dado request válido, quando converter para domínio, então deve criar um novo endereço")
    fun givenValidRequest_whenConvertingToDomain_thenCreateNewEndereco() {
        val endereco = mapper.toDomain(ClienteFixtures.enderecoRequest())

        assertNotNull(endereco.id.value)
        assertEquals("Rua das Flores", endereco.logradouro)
        assertEquals("100", endereco.numero)
        assertEquals("Apto 42", endereco.complemento)
        assertEquals("Centro", endereco.bairro)
        assertEquals("São Paulo", endereco.cidade)
        assertEquals("SP", endereco.estado)
        assertEquals("01001000", endereco.cep)
    }

    @Test
    @DisplayName("Dado request sem complemento, quando converter para domínio, então o complemento deve ser nulo")
    fun givenRequestWithoutComplemento_whenConvertingToDomain_thenComplementoIsNull() {
        assertNull(mapper.toDomain(ClienteFixtures.enderecoRequest(complemento = null)).complemento)
    }

    @Test
    @DisplayName("Dado request com cep em branco, quando converter para domínio, então deve lançar exceção")
    fun givenRequestWithBlankCep_whenConvertingToDomain_thenThrowException() {
        val request = ClienteFixtures.enderecoRequest().copy(cep = "")

        val exception = assertFailsWith<IllegalArgumentException> { mapper.toDomain(request) }

        assertEquals("CEP é obrigatório", exception.message)
    }

    @Test
    @DisplayName("Dado um endereço, quando converter para response, então deve expor os dados")
    fun givenEndereco_whenConvertingToResponse_thenExposeData() {
        val response = mapper.toResponse(ClienteFixtures.endereco())

        assertEquals("Rua das Flores", response.logradouro)
        assertEquals("100", response.numero)
        assertEquals("Apto 42", response.complemento)
        assertEquals("Centro", response.bairro)
        assertEquals("São Paulo", response.cidade)
        assertEquals("SP", response.estado)
        assertEquals("01001000", response.cep)
    }

    @Test
    @DisplayName("Dado um request válido, quando converter ida e volta, então deve preservar os dados")
    fun givenValidRequest_whenConvertingRoundTrip_thenPreserveData() {
        val request = ClienteFixtures.enderecoRequest(complemento = null)

        val response = mapper.toResponse(mapper.toDomain(request))

        assertEquals(request.logradouro, response.logradouro)
        assertEquals(request.numero, response.numero)
        assertNull(response.complemento)
        assertEquals(request.bairro, response.bairro)
        assertEquals(request.cidade, response.cidade)
        assertEquals(request.estado, response.estado)
        assertEquals(request.cep, response.cep)
    }
}
