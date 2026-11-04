package br.com.fiap.oficina.cliente.domain

import br.com.fiap.oficina.cliente.ClienteFixtures
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertFailsWith

@DisplayName("Domínio - Identificadores de Cliente")
class ClienteIdTest {
    @Test
    @DisplayName("Dado nenhum valor, quando gerar identificadores, então devem ser únicos")
    fun givenNoValue_whenGeneratingIds_thenTheyAreUnique() {
        assertNotEquals(ClienteId.generate(), ClienteId.generate())
        assertNotEquals(ContatoId.generate(), ContatoId.generate())
        assertNotEquals(EnderecoId.generate(), EnderecoId.generate())
    }

    @Test
    @DisplayName("Dado um uuid válido, quando converter, então deve preservar o valor")
    fun givenValidUuid_whenConverting_thenPreserveValue() {
        val esperado = UUID.fromString(ClienteFixtures.ID_1)

        assertEquals(esperado, ClienteId.toUUID(ClienteFixtures.ID_1).value)
        assertEquals(esperado, ContatoId.toUUID(ClienteFixtures.ID_1).value)
        assertEquals(esperado, EnderecoId.toUUID(ClienteFixtures.ID_1).value)
    }

    @Test
    @DisplayName("Dado um identificador, quando converter para texto, então deve retornar o uuid")
    fun givenId_whenConvertingToString_thenReturnUuid() {
        assertEquals(ClienteFixtures.ID_1, ClienteId.toUUID(ClienteFixtures.ID_1).toString())
        assertEquals(ClienteFixtures.ID_2, ContatoId.toUUID(ClienteFixtures.ID_2).toString())
        assertEquals(ClienteFixtures.ENDERECO_ID, EnderecoId.toUUID(ClienteFixtures.ENDERECO_ID).toString())
    }

    @Test
    @DisplayName("Dado o mesmo uuid, quando comparar identificadores, então devem ser iguais")
    fun givenSameUuid_whenComparingIds_thenTheyAreEqual() {
        assertEquals(ClienteId.toUUID(ClienteFixtures.ID_1), ClienteId.toUUID(ClienteFixtures.ID_1))
        assertNotEquals(ClienteId.toUUID(ClienteFixtures.ID_1), ClienteId.toUUID(ClienteFixtures.ID_2))
    }

    @Test
    @DisplayName("Dado um texto inválido, quando converter, então deve lançar exceção")
    fun givenInvalidText_whenConverting_thenThrowException() {
        assertFailsWith<IllegalArgumentException> { ClienteId.toUUID("nao-e-um-uuid") }
        assertFailsWith<IllegalArgumentException> { ContatoId.toUUID("nao-e-um-uuid") }
        assertFailsWith<IllegalArgumentException> { EnderecoId.toUUID("nao-e-um-uuid") }
    }
}
