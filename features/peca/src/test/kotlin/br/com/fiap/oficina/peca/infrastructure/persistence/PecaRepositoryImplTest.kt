package br.com.fiap.oficina.peca.infrastructure.persistence

import br.com.fiap.oficina.peca.domain.Peca
import br.com.fiap.oficina.peca.domain.PecaId
import br.com.fiap.oficina.peca.infrastructure.mapper.PecaJPAMapper
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.Optional
import java.util.UUID

@DisplayName("Infraestrutura - Repositório de Peça")
class PecaRepositoryImplTest {
    private val jpaRepositoryMock = mockk<PecaJPARepository>()
    private val mapperMock = mockk<PecaJPAMapper>()
    private val repository = PecaRepositoryImpl(jpaRepositoryMock, mapperMock)

    @Test
    @DisplayName("Dado uma peça, quando salvar, então deve persistir e retornar a peça salva")
    fun givenPeca_whenSaving_thenPersistAndReturnSavedPeca() {
        val peca = peca()
        val jpa = pecaJPA()
        val jpaSalva = pecaJPA(nome = "Nome persistido")
        val pecaSalva = peca(nome = "Nome persistido")

        every { mapperMock.toJpa(peca) } returns jpa
        every { jpaRepositoryMock.save(jpa) } returns jpaSalva
        every { mapperMock.toDomain(jpaSalva) } returns pecaSalva

        assertSame(pecaSalva, repository.salvar(peca))

        verify(exactly = 1) { mapperMock.toJpa(peca) }
        verify(exactly = 1) { jpaRepositoryMock.save(jpa) }
        verify(exactly = 1) { mapperMock.toDomain(jpaSalva) }
    }

    @Test
    @DisplayName("Dado peças cadastradas, quando listar, então deve retornar todas convertidas")
    fun givenRegisteredPecas_whenListing_thenReturnAllConverted() {
        val jpa1 = pecaJPA(id = ID_1, codigo = "PC-001")
        val jpa2 = pecaJPA(id = ID_2, codigo = "PC-002")
        val peca1 = peca(id = ID_1, codigo = "PC-001")
        val peca2 = peca(id = ID_2, codigo = "PC-002")

        every { jpaRepositoryMock.findAll() } returns listOf(jpa1, jpa2)
        every { mapperMock.toDomain(jpa1) } returns peca1
        every { mapperMock.toDomain(jpa2) } returns peca2

        assertEquals(listOf(peca1, peca2), repository.listar())

        verify(exactly = 1) { jpaRepositoryMock.findAll() }
        verify(exactly = 2) { mapperMock.toDomain(any()) }
    }

    @Test
    @DisplayName("Dado nenhuma peça cadastrada, quando listar, então deve retornar lista vazia")
    fun givenNoPecas_whenListing_thenReturnEmptyList() {
        every { jpaRepositoryMock.findAll() } returns emptyList()

        assertTrue(repository.listar().isEmpty())

        verify(exactly = 0) { mapperMock.toDomain(any()) }
    }

    @Test
    @DisplayName("Dado um id existente, quando buscar por id, então deve retornar a peça")
    fun givenExistingId_whenFindingById_thenReturnPeca() {
        val jpa = pecaJPA()
        val peca = peca()

        every { jpaRepositoryMock.findById(UUID.fromString(ID_1)) } returns Optional.of(jpa)
        every { mapperMock.toDomain(jpa) } returns peca

        assertSame(peca, repository.buscarPorId(PecaId.toUUID(ID_1)))

        verify(exactly = 1) { jpaRepositoryMock.findById(UUID.fromString(ID_1)) }
    }

    @Test
    @DisplayName("Dado um id inexistente, quando buscar por id, então deve retornar nulo")
    fun givenNonExistentId_whenFindingById_thenReturnNull() {
        every { jpaRepositoryMock.findById(UUID.fromString(ID_1)) } returns Optional.empty()

        assertNull(repository.buscarPorId(PecaId.toUUID(ID_1)))

        verify(exactly = 0) { mapperMock.toDomain(any()) }
    }

    @Test
    @DisplayName("Dado um código existente, quando buscar por código, então deve retornar a peça")
    fun givenExistingCodigo_whenFindingByCodigo_thenReturnPeca() {
        val jpa = pecaJPA()
        val peca = peca()

        every { jpaRepositoryMock.findByCodigo(CODIGO) } returns jpa
        every { mapperMock.toDomain(jpa) } returns peca

        assertSame(peca, repository.buscarPorCodigo(CODIGO))

        verify(exactly = 1) { jpaRepositoryMock.findByCodigo(CODIGO) }
    }

    @Test
    @DisplayName("Dado um código inexistente, quando buscar por código, então deve retornar nulo")
    fun givenNonExistentCodigo_whenFindingByCodigo_thenReturnNull() {
        every { jpaRepositoryMock.findByCodigo(CODIGO) } returns null

        assertNull(repository.buscarPorCodigo(CODIGO))

        verify(exactly = 0) { mapperMock.toDomain(any()) }
    }

    private fun peca(
        id: String = ID_1,
        codigo: String = CODIGO,
        nome: String = NOME,
    ) = Peca.reconstruir(
        id = id,
        codigo = codigo,
        nome = nome,
        descricao = DESCRICAO,
        fabricante = FABRICANTE,
        fornecedor = FORNECEDOR,
        precoCompra = PRECO_COMPRA,
        precoVenda = PRECO_VENDA,
        quantidade = 10,
        ativo = true,
    )

    private fun pecaJPA(
        id: String = ID_1,
        codigo: String = CODIGO,
        nome: String = NOME,
    ) = PecaJPA(
        id = UUID.fromString(id),
        codigo = codigo,
        nome = nome,
        descricao = DESCRICAO,
        fabricante = FABRICANTE,
        fornecedor = FORNECEDOR,
        precoCompra = PRECO_COMPRA,
        precoVenda = PRECO_VENDA,
        quantidade = 10,
        ativo = true,
    )

    private companion object {
        const val ID_1 = "00000000-0000-0000-0000-000000000100"
        const val ID_2 = "00000000-0000-0000-0000-000000000200"
        const val CODIGO = "PC-001"
        const val NOME = "Pastilha de freio"
        const val DESCRICAO = "Pastilha dianteira"
        const val FABRICANTE = "Bosch"
        const val FORNECEDOR = "AutoPeças RJ"
        val PRECO_COMPRA: BigDecimal = BigDecimal("50.00")
        val PRECO_VENDA: BigDecimal = BigDecimal("89.90")
    }
}
