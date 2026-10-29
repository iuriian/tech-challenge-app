package br.com.fiap.oficina.api.exception

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.Instant

@DisplayName("Contrato de erro da API")
class ErrorResponsesTest {
    private val momento = Instant.parse("2026-10-27T13:45:30Z")

    private fun erro() = ErrorResponse(
        timestamp = momento,
        status = 404,
        error = "Recurso não encontrado",
        message = "Cliente não encontrado",
        path = "/api/v2/clientes/42",
    )

    private fun validacao() = ValidationErrorResponse(
        timestamp = momento,
        status = 400,
        error = "Dados inválidos",
        message = "Os dados fornecidos são inválidos",
        path = "/api/v2/funcionarios",
        fields = mapOf("nome" to "não deve estar em branco"),
    )

    @Test
    @DisplayName("Dado um ErrorResponse, quando construido sem timestamp, entao deve usar o instante atual")
    fun givenErrorResponse_whenBuiltWithoutTimestamp_thenUseCurrentInstant() {
        val antes = Instant.now()

        val resposta = ErrorResponse(status = 500, error = "Erro", message = "Falhou", path = "/x")

        assertTrue(!resposta.timestamp.isBefore(antes))
        assertEquals(500, resposta.status)
    }

    @Test
    @DisplayName("Dado um ErrorResponse, quando desestruturado, entao deve expor todos os componentes")
    fun givenErrorResponse_whenDestructured_thenExposeAllComponents() {
        val (timestamp, status, error, message, path) = erro()

        assertEquals(momento, timestamp)
        assertEquals(404, status)
        assertEquals("Recurso não encontrado", error)
        assertEquals("Cliente não encontrado", message)
        assertEquals("/api/v2/clientes/42", path)
    }

    @Test
    @DisplayName("Dado dois ErrorResponse iguais, quando comparados, entao devem ser equivalentes")
    fun givenEqualErrorResponses_whenCompared_thenBeEquivalent() {
        val original = erro()

        assertEquals(original, original)
        assertEquals(original, erro())
        assertEquals(original.hashCode(), erro().hashCode())
        assertEquals(original, original.copy())
    }

    @Test
    @DisplayName("Dado ErrorResponse divergentes em um campo, quando comparados, entao devem diferir")
    fun givenErrorResponsesDifferingInOneField_whenCompared_thenDiffer() {
        val original = erro()

        assertNotEquals(original, original.copy(timestamp = momento.plusSeconds(1)))
        assertNotEquals(original, original.copy(status = 409))
        assertNotEquals(original, original.copy(error = "Outro"))
        assertNotEquals(original, original.copy(message = "Outra"))
        assertNotEquals(original, original.copy(path = "/outro"))
        assertNotEquals(original as Any?, null as Any?)
        assertNotEquals(original as Any?, "ErrorResponse" as Any?)
    }

    @Test
    @DisplayName("Dado um ErrorResponse, quando convertido em texto, entao deve citar os campos do contrato")
    fun givenErrorResponse_whenStringified_thenMentionContractFields() {
        val texto = erro().toString()

        assertTrue(texto.contains("status=404"))
        assertTrue(texto.contains("path=/api/v2/clientes/42"))
    }

    @Test
    @DisplayName("Dado um ValidationErrorResponse, quando construido sem timestamp, entao deve usar o instante atual")
    fun givenValidationErrorResponse_whenBuiltWithoutTimestamp_thenUseCurrentInstant() {
        val antes = Instant.now()

        val resposta =
            ValidationErrorResponse(
                status = 400,
                error = "Dados inválidos",
                message = "Os dados fornecidos são inválidos",
                path = "/x",
                fields = emptyMap(),
            )

        assertTrue(!resposta.timestamp.isBefore(antes))
        assertTrue(resposta.fields.isEmpty())
    }

    @Test
    @DisplayName("Dado um ValidationErrorResponse, quando desestruturado, entao deve expor todos os componentes")
    fun givenValidationErrorResponse_whenDestructured_thenExposeAllComponents() {
        val (timestamp, status, error, message, path, fields) = validacao()

        assertEquals(momento, timestamp)
        assertEquals(400, status)
        assertEquals("Dados inválidos", error)
        assertEquals("Os dados fornecidos são inválidos", message)
        assertEquals("/api/v2/funcionarios", path)
        assertEquals(mapOf("nome" to "não deve estar em branco"), fields)
    }

    @Test
    @DisplayName("Dado dois ValidationErrorResponse iguais, quando comparados, entao devem ser equivalentes")
    fun givenEqualValidationErrorResponses_whenCompared_thenBeEquivalent() {
        val original = validacao()

        assertEquals(original, original)
        assertEquals(original, validacao())
        assertEquals(original.hashCode(), validacao().hashCode())
        assertEquals(original, original.copy())
    }

    @Test
    @DisplayName("Dado ValidationErrorResponse divergentes em um campo, quando comparados, entao devem diferir")
    fun givenValidationErrorResponsesDifferingInOneField_whenCompared_thenDiffer() {
        val original = validacao()

        assertNotEquals(original, original.copy(timestamp = momento.plusSeconds(1)))
        assertNotEquals(original, original.copy(status = 422))
        assertNotEquals(original, original.copy(error = "Outro"))
        assertNotEquals(original, original.copy(message = "Outra"))
        assertNotEquals(original, original.copy(path = "/outro"))
        assertNotEquals(original, original.copy(fields = emptyMap()))
        assertNotEquals(original as Any?, null as Any?)
        assertNotEquals(original as Any?, "ValidationErrorResponse" as Any?)
    }

    @Test
    @DisplayName("Dado um ValidationErrorResponse, quando convertido em texto, entao deve citar os campos invalidos")
    fun givenValidationErrorResponse_whenStringified_thenMentionInvalidFields() {
        val texto = validacao().toString()

        assertTrue(texto.contains("status=400"))
        assertTrue(texto.contains("nome=não deve estar em branco"))
    }
}
