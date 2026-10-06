package br.com.fiap.oficina.peca.infrastructure.persistence

import br.com.fiap.oficina.peca.application.dto.EstoquePecaRequest
import br.com.fiap.oficina.peca.application.mapper.PecaMapper
import br.com.fiap.oficina.peca.application.usecase.EstoquePecasUseCase
import br.com.fiap.oficina.peca.application.usecase.EstoquePecasUseCaseImpl
import br.com.fiap.oficina.peca.domain.Peca
import br.com.fiap.oficina.peca.domain.PecaRepository
import br.com.fiap.oficina.peca.infrastructure.config.PecaConfig
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Import
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.testcontainers.containers.PostgreSQLContainer
import java.math.BigDecimal
import java.util.concurrent.Callable
import java.util.concurrent.CyclicBarrier
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * Prova que o estoque não sofre lost update sob concorrência real.
 *
 * `Propagation.NOT_SUPPORTED` desliga a transação que o Spring normalmente abre em volta do
 * método de teste: sem isso os dados do preparo ficariam não commitados e invisíveis para as
 * outras threads, e cada `executar` rodaria na transação do teste em vez da sua própria.
 */
@DataJpaTest(properties = ["spring.jpa.hibernate.ddl-auto=create-drop"])
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(PecaConfig::class, PecaMapper::class, EstoquePecasUseCaseImpl::class)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@DisplayName("Infraestrutura - Estoque sob concorrência (integração)")
class EstoqueConcorrenteIntegrationTest {
    @Autowired
    private lateinit var useCase: EstoquePecasUseCase

    @Autowired
    private lateinit var repository: PecaRepository

    @Autowired
    private lateinit var jpaRepository: PecaJPARepository

    @BeforeEach
    fun limpar() {
        jpaRepository.deleteAll()
    }

    /**
     * Estoque 10, duas retiradas simultâneas de 6. Só uma pode passar — a segunda precisa
     * enxergar o saldo 4 já commitado e ser recusada.
     *
     * Sem `@Transactional` + trava no use case, as duas leem 10, as duas validam saldo e as duas
     * gravam 4: zero falhas, duas baixas de 6 num estoque de 10. É esse o cenário que o teste
     * flagra — o saldo final é 4 nos dois casos, o que denuncia é a contagem de sucessos.
     */
    @Test
    @DisplayName("Dado duas retiradas simultâneas, quando só há saldo para uma, então a outra deve falhar")
    fun givenTwoSimultaneousWithdrawals_whenStockCoversOnlyOne_thenTheOtherFails() {
        val peca = repository.salvar(novaPeca(quantidade = 10))

        val resultados = executarEmParalelo { useCase.executar(CODIGO, EstoquePecaRequest(quantidade = -6)) }

        val sucessos = resultados.count { it.isSuccess }
        val falhas = resultados.filter { it.isFailure }

        assertEquals(1, sucessos, "exatamente uma retirada pode ter sucesso")
        assertEquals(1, falhas.size)
        assertEquals(
            "Quantidade em estoque insuficiente",
            falhas.single().exceptionOrNull()?.message,
        )
        assertEquals(4, repository.buscarPorId(peca.id)?.quantidade)
    }

    /**
     * Duas reposições simultâneas de 5 sobre estoque 10 devem somar: 20. Com lost update, uma
     * sobrescreveria a outra e o saldo pararia em 15.
     */
    @Test
    @DisplayName("Dado duas reposições simultâneas, quando ambas são válidas, então devem somar ao estoque")
    fun givenTwoSimultaneousReplenishments_whenBothValid_thenBothAddToStock() {
        val peca = repository.salvar(novaPeca(quantidade = 10))

        val resultados = executarEmParalelo { useCase.executar(CODIGO, EstoquePecaRequest(quantidade = 5)) }

        assertEquals(2, resultados.count { it.isSuccess })
        assertEquals(20, repository.buscarPorId(peca.id)?.quantidade)
    }

    /** Dispara [acao] em duas threads liberadas ao mesmo tempo por uma barreira. */
    private fun executarEmParalelo(acao: () -> Unit): List<Result<Unit>> {
        val barreira = CyclicBarrier(THREADS)
        val executor = Executors.newFixedThreadPool(THREADS)

        return try {
            val tarefas =
                List(THREADS) {
                    Callable {
                        barreira.await(TIMEOUT_SEGUNDOS, TimeUnit.SECONDS)
                        runCatching { acao() }
                    }
                }

            executor.invokeAll(tarefas, TIMEOUT_SEGUNDOS, TimeUnit.SECONDS).map { it.get() }
        } finally {
            executor.shutdownNow()
        }
    }

    private fun novaPeca(quantidade: Int) = Peca.criar(
        codigo = CODIGO,
        nome = "Pastilha de freio",
        descricao = "Pastilha dianteira",
        fabricante = "Bosch",
        fornecedor = "AutoPeças RJ",
        precoCompra = BigDecimal("50.00"),
        precoVenda = BigDecimal("89.90"),
        quantidade = quantidade,
    )

    companion object {
        private const val CODIGO = "PC-001"
        private const val THREADS = 2
        private const val TIMEOUT_SEGUNDOS = 10L

        @JvmStatic
        @ServiceConnection
        val postgres: PostgreSQLContainer<*> =
            PostgreSQLContainer("postgres:16-alpine").apply { start() }
    }
}
