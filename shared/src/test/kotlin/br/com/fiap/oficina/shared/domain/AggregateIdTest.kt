package br.com.fiap.oficina.shared.domain

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertFailsWith

@DisplayName("Shared kernel - AggregateId")
class AggregateIdTest {
    private data class TesteId(override val value: UUID) : AggregateId()

    @Test
    @DisplayName("Dado dois ids gerados, quando comparar, então devem ser diferentes")
    fun givenTwoGeneratedIds_whenComparing_thenShouldBeDifferent() {
        assertNotEquals(AggregateId.newId(), AggregateId.newId())
    }

    @Test
    @DisplayName("Dado um uuid válido, quando converter, então deve preservar o valor")
    fun givenValidUuid_whenParsing_thenPreserveValue() {
        assertEquals(UUID.fromString(UUID_VALIDO), AggregateId.parse(UUID_VALIDO))
    }

    @Test
    @DisplayName("Dado um uuid inválido, quando converter, então deve lançar exceção")
    fun givenInvalidUuid_whenParsing_thenThrowException() {
        assertFailsWith<IllegalArgumentException> { AggregateId.parse("nao-e-um-uuid") }
    }

    @Test
    @DisplayName("Dado um uuid vazio, quando converter, então deve lançar exceção")
    fun givenEmptyUuid_whenParsing_thenThrowException() {
        assertFailsWith<IllegalArgumentException> { AggregateId.parse("") }
    }

    @Test
    @DisplayName("Dado um id, quando converter para texto, então deve retornar o uuid")
    fun givenId_whenConvertingToString_thenReturnUuid() {
        val id = TesteId(AggregateId.parse(UUID_VALIDO))

        assertEquals(UUID_VALIDO, id.toString())
        assertEquals(UUID_VALIDO, id.value.toString())
    }

    @Test
    @DisplayName("Dado dois ids com o mesmo valor, quando comparar, então devem ser iguais")
    fun givenTwoIdsWithSameValue_whenComparing_thenShouldBeEqual() {
        assertEquals(TesteId(AggregateId.parse(UUID_VALIDO)), TesteId(AggregateId.parse(UUID_VALIDO)))
    }

    private companion object {
        const val UUID_VALIDO = "00000000-0000-0000-0000-000000000100"
    }
}
