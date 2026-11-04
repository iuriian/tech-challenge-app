package br.com.fiap.oficina.cliente.application.mapper

import br.com.fiap.oficina.cliente.ClienteFixtures
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

@DisplayName("Mapper - Contato")
class ContatoMapperTest {
    private val mapper = ContatoMapper()

    @Test
    @DisplayName("Dado request válido, quando converter para domínio, então deve criar um novo contato")
    fun givenValidRequest_whenConvertingToDomain_thenCreateNewContato() {
        val contato = mapper.toDomain(ClienteFixtures.contatoRequest())

        assertNotNull(contato.id.value)
        assertEquals("CELULAR", contato.tipo)
        assertEquals("João Silva", contato.nome)
        assertEquals("11999998888", contato.telefone)
    }

    @Test
    @DisplayName("Dado request com tipo em branco, quando converter para domínio, então deve lançar exceção")
    fun givenRequestWithBlankTipo_whenConvertingToDomain_thenThrowException() {
        val exception =
            assertFailsWith<IllegalArgumentException> {
                mapper.toDomain(ClienteFixtures.contatoRequest(tipo = ""))
            }

        assertEquals("Tipo do contato é obrigatório", exception.message)
    }

    @Test
    @DisplayName("Dado um contato, quando converter para response, então deve expor os dados")
    fun givenContato_whenConvertingToResponse_thenExposeData() {
        val response = mapper.toResponse(ClienteFixtures.contato())

        assertEquals("CELULAR", response.tipo)
        assertEquals("João Silva", response.nome)
        assertEquals("11999998888", response.telefone)
    }

    @Test
    @DisplayName("Dado um request válido, quando converter ida e volta, então deve preservar os dados")
    fun givenValidRequest_whenConvertingRoundTrip_thenPreserveData() {
        val request = ClienteFixtures.contatoRequest(tipo = "COMERCIAL")

        val response = mapper.toResponse(mapper.toDomain(request))

        assertEquals(request.tipo, response.tipo)
        assertEquals(request.nome, response.nome)
        assertEquals(request.telefone, response.telefone)
    }
}
