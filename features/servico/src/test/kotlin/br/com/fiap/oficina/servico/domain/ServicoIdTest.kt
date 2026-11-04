package br.com.fiap.oficina.servico.domain

import br.com.fiap.oficina.servico.ServicoFixtures
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertFailsWith

@DisplayName("Domínio - Id de Serviço")
class ServicoIdTest {
    @Test
    @DisplayName("Dado uma geração, quando criar ids, então devem ser distintos")
    fun givenGeneration_whenCreatingIds_thenTheyDiffer() {
        assertNotEquals(ServicoId.generate(), ServicoId.generate())
    }

    @Test
    @DisplayName("Dado um uuid válido, quando converter, então deve preservar o valor")
    fun givenValidUuid_whenConverting_thenPreserveValue() {
        val id = ServicoId.toUUID(ServicoFixtures.ID_1)

        assertEquals(UUID.fromString(ServicoFixtures.ID_1), id.value)
        assertEquals(ServicoFixtures.ID_1, id.toString())
    }

    @Test
    @DisplayName("Dado um uuid inválido, quando converter, então deve lançar exceção")
    fun givenInvalidUuid_whenConverting_thenThrowException() {
        assertFailsWith<IllegalArgumentException> { ServicoId.toUUID("nao-e-um-uuid") }
    }

    @Test
    @DisplayName("Dado o mesmo uuid, quando comparar dois ids, então devem ser iguais")
    fun givenSameUuid_whenComparingTwoIds_thenTheyAreEqual() {
        assertEquals(ServicoId.toUUID(ServicoFixtures.ID_1), ServicoId.toUUID(ServicoFixtures.ID_1))
    }
}
