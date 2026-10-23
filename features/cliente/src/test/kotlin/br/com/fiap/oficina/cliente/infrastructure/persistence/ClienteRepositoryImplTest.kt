package br.com.fiap.oficina.cliente.infrastructure.persistence

import br.com.fiap.oficina.cliente.ClienteFixtures
import br.com.fiap.oficina.cliente.domain.ClienteId
import br.com.fiap.oficina.cliente.infrastructure.mapper.ClienteJPAMapper
import br.com.fiap.oficina.shared.domain.valueobject.Document
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.Optional
import java.util.UUID

@DisplayName("Infraestrutura - Repositório de Cliente")
class ClienteRepositoryImplTest {
    private val jpaRepositoryMock = mockk<ClienteJPARepository>()
    private val mapperMock = mockk<ClienteJPAMapper>()
    private val repository = ClienteRepositoryImpl(jpaRepositoryMock, mapperMock)

    @Test
    @DisplayName("Dado um cliente, quando salvar, então deve persistir e retornar o cliente salvo")
    fun givenCliente_whenSaving_thenPersistAndReturnSavedCliente() {
        val cliente = ClienteFixtures.cliente()
        val jpa = ClienteFixtures.clienteJPA()
        val jpaSalvo = jpa.copy(nome = "Nome persistido")
        val clienteSalvo = ClienteFixtures.cliente(nome = "Nome persistido")

        every { mapperMock.toJPA(cliente) } returns jpa
        every { jpaRepositoryMock.save(jpa) } returns jpaSalvo
        every { mapperMock.toDomain(jpaSalvo) } returns clienteSalvo

        val resultado = repository.salvar(cliente)

        assertSame(clienteSalvo, resultado)

        verify(exactly = 1) { mapperMock.toJPA(cliente) }
        verify(exactly = 1) { jpaRepositoryMock.save(jpa) }
        verify(exactly = 1) { mapperMock.toDomain(jpaSalvo) }
    }

    @Test
    @DisplayName("Dado clientes cadastrados, quando listar todos, então deve retornar todos convertidos")
    fun givenRegisteredClientes_whenListingAll_thenReturnAllConverted() {
        val jpa1 = ClienteFixtures.clienteJPA()
        val jpa2 = ClienteFixtures.clienteJPA(id = ClienteFixtures.ID_2, documento = ClienteFixtures.CPF_OUTRO)
        val cliente1 = ClienteFixtures.cliente()
        val cliente2 = ClienteFixtures.cliente(id = ClienteFixtures.ID_2, documento = ClienteFixtures.CPF_OUTRO)

        every { jpaRepositoryMock.findAll() } returns listOf(jpa1, jpa2)
        every { mapperMock.toDomain(jpa1) } returns cliente1
        every { mapperMock.toDomain(jpa2) } returns cliente2

        val resultado = repository.listarTodos()

        assertEquals(listOf(cliente1, cliente2), resultado)

        verify(exactly = 1) { jpaRepositoryMock.findAll() }
        verify(exactly = 2) { mapperMock.toDomain(any()) }
    }

    @Test
    @DisplayName("Dado nenhum cliente cadastrado, quando listar todos, então deve retornar lista vazia")
    fun givenNoClientes_whenListingAll_thenReturnEmptyList() {
        every { jpaRepositoryMock.findAll() } returns emptyList()

        assertTrue(repository.listarTodos().isEmpty())

        verify(exactly = 0) { mapperMock.toDomain(any()) }
    }

    @Test
    @DisplayName("Dado um id existente, quando buscar por id, então deve retornar o cliente convertido")
    fun givenExistingId_whenSearchingById_thenReturnConvertedCliente() {
        val jpa = ClienteFixtures.clienteJPA()
        val cliente = ClienteFixtures.cliente()
        val id = ClienteId.toUUID(ClienteFixtures.ID_1)

        every { jpaRepositoryMock.findById(id.value) } returns Optional.of(jpa)
        every { mapperMock.toDomain(jpa) } returns cliente

        assertSame(cliente, repository.buscarPorId(id))

        verify(exactly = 1) { jpaRepositoryMock.findById(id.value) }
    }

    @Test
    @DisplayName("Dado um id inexistente, quando buscar por id, então deve retornar nulo")
    fun givenNonExistentId_whenSearchingById_thenReturnNull() {
        every { jpaRepositoryMock.findById(any<UUID>()) } returns Optional.empty()

        assertNull(repository.buscarPorId(ClienteId.toUUID(ClienteFixtures.ID_1)))

        verify(exactly = 0) { mapperMock.toDomain(any()) }
    }

    @Test
    @DisplayName("Dado um documento cadastrado, quando buscar por documento, então deve retornar o cliente convertido")
    fun givenRegisteredDocumento_whenSearchingByDocumento_thenReturnConvertedCliente() {
        val jpa = ClienteFixtures.clienteJPA()
        val cliente = ClienteFixtures.cliente()

        every { jpaRepositoryMock.findByDocumentoNumero(ClienteFixtures.CPF) } returns jpa
        every { mapperMock.toDomain(jpa) } returns cliente

        assertSame(cliente, repository.buscarPorDocumento(Document.from(ClienteFixtures.CPF)))

        verify(exactly = 1) { jpaRepositoryMock.findByDocumentoNumero(ClienteFixtures.CPF) }
    }

    @Test
    @DisplayName("Dado um documento não cadastrado, quando buscar por documento, então deve retornar nulo")
    fun givenUnregisteredDocumento_whenSearchingByDocumento_thenReturnNull() {
        every { jpaRepositoryMock.findByDocumentoNumero(any()) } returns null

        assertNull(repository.buscarPorDocumento(Document.from(ClienteFixtures.CPF)))

        verify(exactly = 0) { mapperMock.toDomain(any()) }
    }

    @Test
    @DisplayName("Dado um nome cadastrado, quando buscar por nome, então deve retornar o cliente convertido")
    fun givenRegisteredNome_whenSearchingByNome_thenReturnConvertedCliente() {
        val jpa = ClienteFixtures.clienteJPA()
        val cliente = ClienteFixtures.cliente()

        every { jpaRepositoryMock.findByNome(ClienteFixtures.NOME) } returns jpa
        every { mapperMock.toDomain(jpa) } returns cliente

        assertSame(cliente, repository.buscarPorNome(ClienteFixtures.NOME))

        verify(exactly = 1) { jpaRepositoryMock.findByNome(ClienteFixtures.NOME) }
    }

    @Test
    @DisplayName("Dado um nome não cadastrado, quando buscar por nome, então deve retornar nulo")
    fun givenUnregisteredNome_whenSearchingByNome_thenReturnNull() {
        every { jpaRepositoryMock.findByNome(any()) } returns null

        assertNull(repository.buscarPorNome("Inexistente"))

        verify(exactly = 0) { mapperMock.toDomain(any()) }
    }

    @Test
    @DisplayName("Dado um id, quando deletar, então deve delegar ao repositório JPA")
    fun givenId_whenDeleting_thenDelegateToJpaRepository() {
        val id = ClienteId.toUUID(ClienteFixtures.ID_1)

        every { jpaRepositoryMock.deleteById(id.value) } just runs

        repository.deletar(id)

        verify(exactly = 1) { jpaRepositoryMock.deleteById(id.value) }
    }
}
