package br.com.fiap.oficina.shared.domain.mapper

import br.com.fiap.oficina.shared.domain.valueobject.CPF
import br.com.fiap.oficina.shared.domain.valueobject.Document
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

@DisplayName("Shared kernel - Contrato do ApplicationMapper")
class ApplicationMapperTest {
    private data class ClienteRequest(val documento: String)

    private data class ClienteResponse(val documento: String)

    private data class Cliente(val documento: Document)

    private class ClienteMapper : ApplicationMapper<Cliente, ClienteRequest, ClienteResponse> {
        override fun toDomain(request: ClienteRequest): Cliente = Cliente(Document.from(request.documento))

        override fun toResponse(domain: Cliente): ClienteResponse = ClienteResponse(domain.documento.formatado())
    }

    private val mapper: ApplicationMapper<Cliente, ClienteRequest, ClienteResponse> = ClienteMapper()

    @Test
    @DisplayName("Dada uma requisição, quando converter para domínio, então deve montar o agregado")
    fun givenRequest_whenConvertingToDomain_thenBuildAggregate() {
        val cliente = mapper.toDomain(ClienteRequest(CPF_FORMATADO))

        assertEquals(Cliente(CPF(CPF_VALIDO)), cliente)
    }

    @Test
    @DisplayName("Dado um domínio, quando converter para resposta, então deve expor os dados formatados")
    fun givenDomain_whenConvertingToResponse_thenExposeFormattedData() {
        val response = mapper.toResponse(Cliente(CPF(CPF_VALIDO)))

        assertEquals(ClienteResponse(CPF_FORMATADO), response)
    }

    @Test
    @DisplayName("Dada uma requisição, quando converter nos dois sentidos, então deve preservar o valor")
    fun givenRequest_whenConvertingBothWays_thenPreserveValue() {
        val response = mapper.toResponse(mapper.toDomain(ClienteRequest(CPF_VALIDO)))

        assertEquals(CPF_FORMATADO, response.documento)
    }

    @Test
    @DisplayName("Dada uma requisição inválida, quando converter para domínio, então deve propagar a exceção")
    fun givenInvalidRequest_whenConvertingToDomain_thenPropagateException() {
        val exception = assertFailsWith<IllegalArgumentException> { mapper.toDomain(ClienteRequest("123")) }

        assertEquals("Documento inválido: 123", exception.message)
    }

    private companion object {
        const val CPF_VALIDO = "01234567890"
        const val CPF_FORMATADO = "012.345.678-90"
    }
}
