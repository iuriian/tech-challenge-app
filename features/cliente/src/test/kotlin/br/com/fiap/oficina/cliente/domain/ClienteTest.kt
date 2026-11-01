package br.com.fiap.oficina.cliente.domain

import br.com.fiap.oficina.cliente.ClienteFixtures
import br.com.fiap.oficina.shared.domain.valueobject.CPF
import br.com.fiap.oficina.shared.domain.valueobject.Document
import br.com.fiap.oficina.shared.domain.valueobject.TipoDocumento
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

@DisplayName("Domínio - Cliente")
class ClienteTest {
    @Test
    @DisplayName("Dado dados válidos, quando criar cliente, então deve gerar um novo identificador")
    fun givenValidData_whenCreatingCliente_thenGenerateNewId() {
        val cliente =
            Cliente.criar(
                nome = ClienteFixtures.NOME,
                documento = Document.from(ClienteFixtures.CPF),
                email = ClienteFixtures.EMAIL,
                endereco = ClienteFixtures.endereco(),
                contatos = listOf(ClienteFixtures.contato()),
            )

        assertNotNull(cliente.id.value)
        assertEquals(ClienteFixtures.NOME, cliente.nome)
        assertEquals(ClienteFixtures.CPF, cliente.documento.valor)
        assertEquals(ClienteFixtures.EMAIL, cliente.email)
        assertEquals("Rua das Flores", cliente.endereco?.logradouro)
        assertEquals(1, cliente.contatos.size)
    }

    @Test
    @DisplayName("Dado dados mínimos, quando criar cliente, então endereço e contatos devem ser vazios")
    fun givenMinimalData_whenCreatingCliente_thenEnderecoAndContatosAreEmpty() {
        val cliente =
            Cliente.criar(
                nome = ClienteFixtures.NOME,
                documento = Document.from(ClienteFixtures.CNPJ),
                email = ClienteFixtures.EMAIL,
            )

        assertNull(cliente.endereco)
        assertTrue(cliente.contatos.isEmpty())
        assertEquals(TipoDocumento.CNPJ, cliente.documento.tipo)
    }

    @Test
    @DisplayName("Dado dois clientes criados, quando comparar, então os identificadores devem ser distintos")
    fun givenTwoCreatedClientes_whenComparing_thenIdsAreDistinct() {
        val primeiro =
            Cliente.criar(
                nome = ClienteFixtures.NOME,
                documento = Document.from(ClienteFixtures.CPF),
                email = ClienteFixtures.EMAIL,
            )
        val segundo =
            Cliente.criar(
                nome = ClienteFixtures.NOME,
                documento = Document.from(ClienteFixtures.CPF),
                email = ClienteFixtures.EMAIL,
            )

        assertNotEquals(primeiro.id, segundo.id)
        assertNotEquals(primeiro, segundo)
    }

    @Test
    @DisplayName("Dado um identificador existente, quando reconstruir cliente, então deve preservar o identificador")
    fun givenExistingId_whenReconstructingCliente_thenPreserveId() {
        val cliente = ClienteFixtures.cliente()

        assertEquals(ClienteFixtures.ID_1, cliente.id.toString())
        assertEquals(ClienteFixtures.NOME, cliente.nome)
    }

    @Test
    @DisplayName("Dado nome em branco, quando criar cliente, então deve lançar exceção")
    fun givenBlankNome_whenCreatingCliente_thenThrowException() {
        val exception =
            assertFailsWith<IllegalArgumentException> {
                Cliente.criar(
                    nome = "  ",
                    documento = Document.from(ClienteFixtures.CPF),
                    email = ClienteFixtures.EMAIL,
                )
            }

        assertEquals("Nome do cliente é obrigatório", exception.message)
    }

    @Test
    @DisplayName("Dado e-mail em branco, quando criar cliente, então deve lançar exceção")
    fun givenBlankEmail_whenCreatingCliente_thenThrowException() {
        val exception =
            assertFailsWith<IllegalArgumentException> {
                Cliente.criar(
                    nome = ClienteFixtures.NOME,
                    documento = Document.from(ClienteFixtures.CPF),
                    email = "",
                )
            }

        assertEquals("E-mail do cliente é obrigatório", exception.message)
    }

    @Test
    @DisplayName("Dado um cliente existente, quando atualizar, então deve manter o identificador e trocar os dados")
    fun givenExistingCliente_whenUpdating_thenKeepIdAndReplaceData() {
        val cliente = ClienteFixtures.cliente()
        val novoEndereco = ClienteFixtures.endereco(complemento = null)
        val novosContatos = listOf(ClienteFixtures.contato(tipo = "COMERCIAL"))

        val atualizado =
            cliente.atualizar(
                nome = "Maria Souza",
                documento = CPF.from(ClienteFixtures.CPF_OUTRO),
                email = "maria@email.com",
                endereco = novoEndereco,
                contatos = novosContatos,
            )

        assertEquals(cliente.id, atualizado.id)
        assertEquals("Maria Souza", atualizado.nome)
        assertEquals(ClienteFixtures.CPF_OUTRO, atualizado.documento.valor)
        assertEquals("maria@email.com", atualizado.email)
        assertEquals(novoEndereco, atualizado.endereco)
        assertEquals(novosContatos, atualizado.contatos)
        assertNotEquals(cliente, atualizado)
    }

    @Test
    @DisplayName("Dado um cliente existente, quando atualizar sem endereço e contatos, então deve limpar os dados")
    fun givenExistingCliente_whenUpdatingWithoutEnderecoAndContatos_thenClearData() {
        val cliente = ClienteFixtures.cliente()

        val atualizado =
            cliente.atualizar(
                nome = cliente.nome,
                documento = cliente.documento,
                email = cliente.email,
                endereco = null,
                contatos = emptyList(),
            )

        assertNull(atualizado.endereco)
        assertTrue(atualizado.contatos.isEmpty())
    }
}
