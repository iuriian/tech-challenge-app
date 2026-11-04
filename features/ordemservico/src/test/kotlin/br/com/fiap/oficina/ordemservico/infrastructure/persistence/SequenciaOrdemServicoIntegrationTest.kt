package br.com.fiap.oficina.ordemservico.infrastructure.persistence

import br.com.fiap.oficina.ordemservico.domain.NumeroOrdemServico
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.testcontainers.containers.PostgreSQLContainer

/**
 * Prova que o `nextval` nativo funciona no PostgreSQL e devolve valores crescentes.
 *
 * A sequência é criada aqui porque `ddl-auto=create-drop` gera o schema a partir das
 * entidades e não executa as migrações do Flyway — em produção ela vem da `V11`.
 */
@DataJpaTest(properties = ["spring.jpa.hibernate.ddl-auto=create-drop"])
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("Infraestrutura - Sequência da Ordem de Serviço (integração)")
class SequenciaOrdemServicoIntegrationTest {
    @Autowired
    private lateinit var entityManager: TestEntityManager

    private lateinit var sequencia: SequenciaOrdemServicoImpl

    @BeforeEach
    fun prepararSequencia() {
        entityManager.entityManager
            .createNativeQuery("CREATE SEQUENCE IF NOT EXISTS ordem_servico_numero_seq START WITH 1 INCREMENT BY 1")
            .executeUpdate()

        sequencia = SequenciaOrdemServicoImpl(entityManager.entityManager)
    }

    @Test
    @DisplayName("Dado a sequência criada, quando obter o próximo valor, então deve retornar valores crescentes")
    fun givenCreatedSequence_whenGettingNextValue_thenReturnIncreasingValues() {
        val primeiro = sequencia.proximoValor()
        val segundo = sequencia.proximoValor()

        assertTrue(primeiro > 0)
        assertEquals(primeiro + 1, segundo)
    }

    @Test
    @DisplayName("Dado um valor da sequência, quando compor o número, então deve gerar o formato esperado")
    fun givenSequenceValue_whenComposingNumber_thenGenerateExpectedFormat() {
        val numero = NumeroOrdemServico.criar(sequencial = sequencia.proximoValor(), ano = 2026)

        assertTrue(numero.valor.startsWith("OS-2026-"))
        assertEquals(14, numero.valor.length)
    }

    companion object {
        @JvmStatic
        @ServiceConnection
        val postgres: PostgreSQLContainer<*> = PostgreSQLContainer("postgres:16-alpine")
    }
}
