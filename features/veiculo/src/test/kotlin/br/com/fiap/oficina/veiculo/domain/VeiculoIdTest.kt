package br.com.fiap.oficina.veiculo.domain

import br.com.fiap.oficina.veiculo.VeiculoFixtures
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertFailsWith

@DisplayName("Domínio - Identificadores de Veículo")
class VeiculoIdTest {
    @Test
    @DisplayName("Dado nenhum valor, quando gerar identificadores, então devem ser únicos")
    fun givenNoValue_whenGeneratingIds_thenTheyAreUnique() {
        assertNotEquals(VeiculoId.generate(), VeiculoId.generate())
    }

    @Test
    @DisplayName("Dado um uuid válido, quando converter, então deve preservar o valor")
    fun givenValidUuid_whenConverting_thenPreserveValue() {
        val esperado = UUID.fromString(VeiculoFixtures.ID_1)

        assertEquals(esperado, VeiculoId.toUUID(VeiculoFixtures.ID_1).value)
        assertEquals(esperado, ProprietarioRef.toUUID(VeiculoFixtures.ID_1).value)
    }

    @Test
    @DisplayName("Dado um identificador, quando converter para texto, então deve retornar o uuid")
    fun givenId_whenConvertingToString_thenReturnUuid() {
        assertEquals(VeiculoFixtures.ID_1, VeiculoId.toUUID(VeiculoFixtures.ID_1).toString())
        assertEquals(
            VeiculoFixtures.PROPRIETARIO_ID,
            ProprietarioRef.toUUID(VeiculoFixtures.PROPRIETARIO_ID).toString(),
        )
    }

    @Test
    @DisplayName("Dado o mesmo uuid, quando comparar identificadores, então devem ser iguais")
    fun givenSameUuid_whenComparingIds_thenTheyAreEqual() {
        assertEquals(VeiculoId.toUUID(VeiculoFixtures.ID_1), VeiculoId.toUUID(VeiculoFixtures.ID_1))
        assertNotEquals(VeiculoId.toUUID(VeiculoFixtures.ID_1), VeiculoId.toUUID(VeiculoFixtures.ID_2))

        assertEquals(
            ProprietarioRef.toUUID(VeiculoFixtures.PROPRIETARIO_ID),
            ProprietarioRef.toUUID(VeiculoFixtures.PROPRIETARIO_ID),
        )
        assertNotEquals(
            ProprietarioRef.toUUID(VeiculoFixtures.PROPRIETARIO_ID),
            ProprietarioRef.toUUID(VeiculoFixtures.PROPRIETARIO_ID_2),
        )
    }

    @Test
    @DisplayName("Dado um identificador, quando copiar, então deve preservar o valor")
    fun givenId_whenCopying_thenPreserveValue() {
        val id = VeiculoId.toUUID(VeiculoFixtures.ID_1)
        val ref = ProprietarioRef.toUUID(VeiculoFixtures.PROPRIETARIO_ID)

        assertEquals(id, id.copy())
        assertEquals(id.value, id.component1())
        assertEquals(ref, ref.copy())
        assertEquals(ref.value, ref.component1())
    }

    @Test
    @DisplayName("Dado um texto inválido, quando converter, então deve lançar exceção")
    fun givenInvalidText_whenConverting_thenThrowException() {
        assertFailsWith<IllegalArgumentException> { VeiculoId.toUUID("nao-e-um-uuid") }
        assertFailsWith<IllegalArgumentException> { ProprietarioRef.toUUID("nao-e-um-uuid") }
    }
}
