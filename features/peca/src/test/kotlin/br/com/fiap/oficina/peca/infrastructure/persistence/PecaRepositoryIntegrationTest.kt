package br.com.fiap.oficina.peca.infrastructure.persistence

import br.com.fiap.oficina.peca.domain.Peca
import br.com.fiap.oficina.peca.domain.PecaId
import br.com.fiap.oficina.peca.domain.PecaRepository
import br.com.fiap.oficina.peca.infrastructure.config.PecaConfig
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Import
import org.springframework.dao.DataIntegrityViolationException
import org.testcontainers.containers.PostgreSQLContainer
import java.math.BigDecimal
import java.util.UUID
import kotlin.test.assertFailsWith

@DataJpaTest(properties = ["spring.jpa.hibernate.ddl-auto=create-drop"])
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(PecaConfig::class)
@DisplayName("Infraestrutura - Repositório de Peça (integração)")
class PecaRepositoryIntegrationTest {
    @Autowired
    private lateinit var repository: PecaRepository

    @Autowired
    private lateinit var jpaRepository: PecaJPARepository

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Test
    @DisplayName("Dado uma nova peça, quando salvar, então deve persistir e ser encontrada por id")
    fun givenNewPeca_whenSaving_thenPersistAndFindById() {
        val peca = novaPeca()

        val salva = repository.salvar(peca)
        sincronizar()

        assertEquals(peca, salva)
        assertEquals(peca, repository.buscarPorId(peca.id))
    }

    @Test
    @DisplayName("Dado uma peça existente, quando salvar alterações, então deve atualizar o registro")
    fun givenExistingPeca_whenSavingChanges_thenUpdateRecord() {
        val peca = repository.salvar(novaPeca())
        sincronizar()

        repository.salvar(
            peca.atualizar(
                nome = "Pastilha cerâmica",
                codigo = CODIGO_2,
                descricao = "Dianteira cerâmica",
                fabricante = "Brembo",
                fornecedor = "AutoPeças SP",
                precoCompra = BigDecimal("55.00"),
                precoVenda = BigDecimal("99.90"),
            ),
        )
        sincronizar()

        val atualizada = repository.buscarPorId(peca.id)

        assertEquals(1, jpaRepository.count())
        assertEquals("Pastilha cerâmica", atualizada?.nome)
        assertEquals("Brembo", atualizada?.fabricante)
        assertEquals(BigDecimal("99.90"), atualizada?.precoVenda)
        assertEquals(CODIGO_2, atualizada?.codigo)
    }

    @Test
    @DisplayName("Dado uma peça, quando salvar, então deve preservar a escala dos preços")
    fun givenPeca_whenSaving_thenPreservePriceScale() {
        val peca = repository.salvar(novaPeca(precoCompra = BigDecimal("50.00"), precoVenda = BigDecimal("89.90")))
        sincronizar()

        val lida = repository.buscarPorId(peca.id)

        assertEquals(BigDecimal("50.00"), lida?.precoCompra)
        assertEquals(BigDecimal("89.90"), lida?.precoVenda)
    }

    @Test
    @DisplayName("Dado peças cadastradas, quando listar, então deve retornar todas")
    fun givenRegisteredPecas_whenListing_thenReturnAll() {
        val pecas =
            listOf(
                novaPeca(codigo = CODIGO_1),
                novaPeca(codigo = CODIGO_2, nome = "Filtro de óleo"),
            )
        pecas.forEach(repository::salvar)
        sincronizar()

        assertEquals(pecas.toSet(), repository.listar().toSet())
    }

    @Test
    @DisplayName("Dado nenhuma peça cadastrada, quando listar, então deve retornar lista vazia")
    fun givenNoPecas_whenListing_thenReturnEmptyList() {
        assertTrue(repository.listar().isEmpty())
    }

    @Test
    @DisplayName("Dado um id inexistente, quando buscar por id, então deve retornar nulo")
    fun givenNonExistentId_whenFindingById_thenReturnNull() {
        assertNull(repository.buscarPorId(PecaId.generate()))
    }

    @Test
    @DisplayName("Dado um código cadastrado, quando buscar por código, então deve retornar a peça")
    fun givenRegisteredCodigo_whenFindingByCodigo_thenReturnPeca() {
        val peca = repository.salvar(novaPeca())
        sincronizar()

        assertEquals(peca, repository.buscarPorCodigo(CODIGO_1))
        assertNull(repository.buscarPorCodigo(CODIGO_2))
    }

    @Test
    @DisplayName("Dado uma peça ativada, quando salvar, então deve persistir o status ativo")
    fun givenActivatedPeca_whenSaving_thenPersistActiveStatus() {
        val peca = repository.salvar(novaPeca().ativar())
        sincronizar()

        assertEquals(true, repository.buscarPorId(peca.id)?.ativo)
    }

    @Test
    @DisplayName("Dado uma movimentação de estoque, quando salvar, então deve persistir a nova quantidade")
    fun givenStockMovement_whenSaving_thenPersistNewQuantity() {
        val peca = repository.salvar(novaPeca(quantidade = 10))
        sincronizar()

        repository.salvar(peca.retirarPecas(4))
        sincronizar()

        assertEquals(6, repository.buscarPorId(peca.id)?.quantidade)
    }

    @Test
    @DisplayName("Dado um código já cadastrado, quando salvar, então deve violar a unicidade")
    fun givenCodigoAlreadyRegistered_whenSaving_thenViolateUniqueConstraint() {
        repository.salvar(novaPeca())
        sincronizar()

        assertFailsWith<DataIntegrityViolationException> {
            jpaRepository.saveAndFlush(novaPecaJPA(codigo = CODIGO_1))
        }
    }

    @Test
    @DisplayName("Dado um nome acima de 100 caracteres, quando salvar, então deve violar o tamanho da coluna")
    fun givenNomeLongerThanColumn_whenSaving_thenViolateColumnLength() {
        assertFailsWith<DataIntegrityViolationException> {
            jpaRepository.saveAndFlush(novaPecaJPA(codigo = CODIGO_2, nome = "a".repeat(101)))
        }
    }

    /** Envia as operações pendentes ao banco e limpa o contexto, forçando novas leituras do PostgreSQL. */
    private fun sincronizar() {
        entityManager.flush()
        entityManager.clear()
    }

    private fun novaPeca(
        codigo: String = CODIGO_1,
        nome: String = "Pastilha de freio",
        precoCompra: BigDecimal = BigDecimal("50.00"),
        precoVenda: BigDecimal = BigDecimal("89.90"),
        quantidade: Int = 10,
    ) = Peca.criar(
        codigo = codigo,
        nome = nome,
        descricao = "Pastilha dianteira",
        fabricante = "Bosch",
        fornecedor = "AutoPeças RJ",
        precoCompra = precoCompra,
        precoVenda = precoVenda,
        quantidade = quantidade,
    )

    private fun novaPecaJPA(codigo: String, nome: String = "Pastilha de freio") = PecaJPA(
        id = UUID.randomUUID(),
        codigo = codigo,
        nome = nome,
        descricao = "Pastilha dianteira",
        fabricante = "Bosch",
        fornecedor = "AutoPeças RJ",
        precoCompra = BigDecimal("50.00"),
        precoVenda = BigDecimal("89.90"),
        quantidade = 10,
        ativo = false,
    )

    companion object {
        private const val CODIGO_1 = "PC-001"
        private const val CODIGO_2 = "PC-002"

        @JvmStatic
        @ServiceConnection
        val postgres: PostgreSQLContainer<*> =
            PostgreSQLContainer("postgres:16-alpine").apply { start() }
    }
}
