package br.com.fiap.oficina.cliente.domain

import br.com.fiap.oficina.cliente.ClienteFixtures
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

@DisplayName("Domínio - Contato")
class ContatoTest {
    @Test
    @DisplayName("Dado dados válidos, quando criar contato, então deve gerar um novo identificador")
    fun givenValidData_whenCreatingContato_thenGenerateNewId() {
        val contato = Contato.criar(tipo = "CELULAR", nome = "João Silva", telefone = "11999998888")

        assertNotNull(contato.id.value)
        assertEquals("CELULAR", contato.tipo)
        assertEquals("João Silva", contato.nome)
        assertEquals("11999998888", contato.telefone)
    }

    @Test
    @DisplayName("Dado um identificador existente, quando reconstruir contato, então deve preservar o identificador")
    fun givenExistingId_whenReconstructingContato_thenPreserveId() {
        val contato = ClienteFixtures.contato()

        assertEquals(ClienteFixtures.CONTATO_ID, contato.id.toString())
        assertEquals("CELULAR", contato.tipo)
    }

    @Test
    @DisplayName("Dado tipo em branco, quando criar contato, então deve lançar exceção")
    fun givenBlankTipo_whenCreatingContato_thenThrowException() {
        val exception =
            assertFailsWith<IllegalArgumentException> {
                Contato.criar(tipo = " ", nome = "João Silva", telefone = "11999998888")
            }

        assertEquals("Tipo do contato é obrigatório", exception.message)
    }

    @Test
    @DisplayName("Dado nome em branco, quando criar contato, então deve lançar exceção")
    fun givenBlankNome_whenCreatingContato_thenThrowException() {
        val exception =
            assertFailsWith<IllegalArgumentException> {
                Contato.criar(tipo = "CELULAR", nome = "", telefone = "11999998888")
            }

        assertEquals("Nome do contato é obrigatório", exception.message)
    }

    @Test
    @DisplayName("Dado telefone em branco, quando criar contato, então deve lançar exceção")
    fun givenBlankTelefone_whenCreatingContato_thenThrowException() {
        val exception =
            assertFailsWith<IllegalArgumentException> {
                Contato.criar(tipo = "CELULAR", nome = "João Silva", telefone = "  ")
            }

        assertEquals("Telefone do contato é obrigatório", exception.message)
    }

    @Test
    @DisplayName("Dado contatos com dados diferentes, quando comparar, então não devem ser iguais")
    fun givenContatosWithDifferentData_whenComparing_thenNotEqual() {
        val contato = ClienteFixtures.contato()
        val outro =
            Contato.reconstruir(
                id = contato.id.toString(),
                tipo = contato.tipo,
                nome = contato.nome,
                telefone = "1133334444",
            )

        assertNotEquals(contato, outro)
        assertEquals(contato.id, outro.id)
    }
}
