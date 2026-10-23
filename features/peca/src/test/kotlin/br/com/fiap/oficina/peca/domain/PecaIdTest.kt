package br.com.fiap.oficina.peca.domain

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertFailsWith

@DisplayName("Domínio - PecaId")
class PecaIdTest {
    @Test
    @DisplayName("Dado uma geração de id, quando gerar, então deve produzir um UUID não nulo")
    fun givenIdGeneration_whenGenerating_thenProduceNonNullUuid() {
        assertNotNull(PecaId.generate().value)
    }

    @Test
    @DisplayName("Dado duas gerações, quando gerar ids, então devem ser diferentes")
    fun givenTwoGenerations_whenGeneratingIds_thenShouldBeDifferent() {
        assertNotEquals(PecaId.generate(), PecaId.generate())
    }

    @Test
    @DisplayName("Dado um UUID válido em texto, quando converter, então deve preservar o valor")
    fun givenValidUuidString_whenConverting_thenPreserveValue() {
        assertEquals(UUID.fromString(ID_VALIDO), PecaId.toUUID(ID_VALIDO).value)
    }

    @Test
    @DisplayName("Dado um texto inválido, quando converter, então deve lançar exceção")
    fun givenInvalidString_whenConverting_thenThrowException() {
        assertFailsWith<IllegalArgumentException> { PecaId.toUUID("nao-e-um-uuid") }
    }

    @Test
    @DisplayName("Dado um texto vazio, quando converter, então deve lançar exceção")
    fun givenEmptyString_whenConverting_thenThrowException() {
        assertFailsWith<IllegalArgumentException> { PecaId.toUUID("") }
    }

    @Test
    @DisplayName("Dado um PecaId, quando converter para texto, então deve retornar o UUID em texto")
    fun givenPecaId_whenConvertingToString_thenReturnUuidAsText() {
        assertEquals(ID_VALIDO, PecaId.toUUID(ID_VALIDO).toString())
    }

    @Test
    @DisplayName("Dado dois ids com o mesmo UUID, quando comparar, então devem ser iguais")
    fun givenTwoIdsWithSameUuid_whenComparing_thenShouldBeEqual() {
        val id1 = PecaId.toUUID(ID_VALIDO)
        val id2 = PecaId(UUID.fromString(ID_VALIDO))

        assertEquals(id1, id2)
        assertEquals(id1.hashCode(), id2.hashCode())
    }

    private companion object {
        const val ID_VALIDO = "00000000-0000-0000-0000-000000000100"
    }
}
