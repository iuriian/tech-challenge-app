package br.com.fiap.oficina.ordemservico.infrastructure.persistence

import br.com.fiap.oficina.cliente.application.port.ClienteQueryPort
import br.com.fiap.oficina.funcionario.application.port.FuncionarioQueryPort
import br.com.fiap.oficina.ordemservico.OrdemServicoFixtures
import br.com.fiap.oficina.ordemservico.domain.ClienteRef
import br.com.fiap.oficina.ordemservico.domain.OrdemServico
import br.com.fiap.oficina.ordemservico.domain.OrdemServicoId
import br.com.fiap.oficina.ordemservico.domain.OrdemServicoRepository
import br.com.fiap.oficina.ordemservico.domain.OrdemServicoStatus
import br.com.fiap.oficina.ordemservico.domain.VeiculoRef
import br.com.fiap.oficina.ordemservico.infrastructure.config.OrdemServicoConfig
import br.com.fiap.oficina.peca.application.port.PecaQueryPort
import br.com.fiap.oficina.servico.application.port.ServicoQueryPort
import br.com.fiap.oficina.veiculo.application.port.VeiculoQueryPort
import com.ninjasquad.springmockk.MockkBean
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
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
import org.testcontainers.containers.PostgreSQLContainer
import java.math.BigDecimal

/**
 * Exercita o mapeamento do agregado contra um PostgreSQL real.
 *
 * O ponto central é o `@OneToMany` unidirecional de [OrdemServicoJPA.itens]: como
 * `ItemOrcamento` não tem identidade no domínio, cada `salvar` reescreve a coleção
 * inteira, e a FK `ordem_servico_id` é `NOT NULL`. Só o banco prova que o Hibernate
 * grava a FK no próprio insert do item em vez de inserir nulo e depois atualizar.
 *
 * Os `@MockkBean` dos query ports existem porque [OrdemServicoConfig] também registra
 * os adaptadores de ACL; aqui só a persistência está sob teste.
 */
@DataJpaTest(properties = ["spring.jpa.hibernate.ddl-auto=create-drop"])
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(OrdemServicoConfig::class)
@DisplayName("Infraestrutura - Repositório de Ordem de Serviço (integração)")
class OrdemServicoRepositoryIntegrationTest {
    @MockkBean(relaxed = true)
    private lateinit var clienteQueryPort: ClienteQueryPort

    @MockkBean(relaxed = true)
    private lateinit var funcionarioQueryPort: FuncionarioQueryPort

    @MockkBean(relaxed = true)
    private lateinit var veiculoQueryPort: VeiculoQueryPort

    @MockkBean(relaxed = true)
    private lateinit var pecaQueryPort: PecaQueryPort

    @MockkBean(relaxed = true)
    private lateinit var servicoQueryPort: ServicoQueryPort

    @Autowired
    private lateinit var repository: OrdemServicoRepository

    @Autowired
    private lateinit var jpaRepository: OrdemServicoJPARepository

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Test
    @DisplayName("Dado uma ordem com itens, quando salvar, então deve gravar a ordem e os itens com a FK preenchida")
    fun givenOrdemWithItems_whenSaving_thenPersistOrdemAndItemsWithForeignKey() {
        val ordemServico = OrdemServicoFixtures.ordemServico()

        repository.salvar(ordemServico)
        sincronizar()

        assertEquals(1, jpaRepository.count())
        assertEquals(2, contarItens())
        assertEquals(0, contarItensOrfaos())
    }

    @Test
    @DisplayName("Dado uma ordem persistida, quando buscar por id, então deve reconstruir o agregado completo")
    fun givenPersistedOrdem_whenFindingById_thenRebuildFullAggregate() {
        val ordemServico = OrdemServicoFixtures.ordemServico()

        repository.salvar(ordemServico)
        sincronizar()

        val encontrada = repository.buscarPorId(ordemServico.id)

        assertEquals(ordemServico, encontrada)
        assertEquals(BigDecimal("329.80"), encontrada?.orcamento?.valorTotal)
    }

    @Test
    @DisplayName("Dado uma ordem sem itens, quando salvar, então deve gravar o orçamento vazio")
    fun givenOrdemWithoutItems_whenSaving_thenPersistEmptyBudget() {
        val ordemServico = OrdemServicoFixtures.ordemServico(orcamento = OrdemServicoFixtures.orcamento(emptyList()))

        repository.salvar(ordemServico)
        sincronizar()

        assertEquals(0, contarItens())
        assertTrue(repository.buscarPorId(ordemServico.id)!!.orcamento.itens.isEmpty())
    }

    @Test
    @DisplayName("Dado uma ordem persistida, quando trocar os itens do orçamento, então deve remover os antigos")
    fun givenPersistedOrdem_whenReplacingBudgetItems_thenRemoveTheOldOnes() {
        val ordemServico = repository.salvar(OrdemServicoFixtures.ordemServico())
        sincronizar()

        val novoOrcamento = OrdemServicoFixtures.orcamento(itens = listOf(OrdemServicoFixtures.itemServico()))
        repository.salvar(ordemServico.copy(orcamento = novoOrcamento))
        sincronizar()

        assertEquals(1, jpaRepository.count())
        assertEquals(1, contarItens())
        assertEquals(novoOrcamento, repository.buscarPorId(ordemServico.id)?.orcamento)
    }

    @Test
    @DisplayName("Dado uma ordem com itens, quando esvaziar o orçamento, então deve remover todos os itens")
    fun givenOrdemWithItems_whenEmptyingBudget_thenRemoveAllItems() {
        val ordemServico = repository.salvar(OrdemServicoFixtures.ordemServico())
        sincronizar()

        repository.salvar(ordemServico.copy(orcamento = OrdemServicoFixtures.orcamento(emptyList())))
        sincronizar()

        assertEquals(1, jpaRepository.count())
        assertEquals(0, contarItens())
    }

    @Test
    @DisplayName("Dado uma ordem persistida, quando alterar o status, então deve gravar as datas de execução")
    fun givenPersistedOrdem_whenChangingStatus_thenPersistExecutionDates() {
        val ordemServico =
            repository.salvar(OrdemServicoFixtures.ordemServico(status = OrdemServicoStatus.AGUARDANDO_APROVACAO))
        sincronizar()

        val emExecucao = ordemServico.alterarStatus(OrdemServicoStatus.EM_EXECUCAO)
        repository.salvar(emExecucao)
        sincronizar()

        val recarregada = repository.buscarPorId(ordemServico.id)

        assertEquals(OrdemServicoStatus.EM_EXECUCAO, recarregada?.status)
        assertNotNull(recarregada?.dataInicioExecucao)
        assertNull(recarregada?.dataFinalizacao)
    }

    @Test
    @DisplayName("Dado uma ordem persistida, quando recarregar, então deve preservar a escala dos valores")
    fun givenPersistedOrdem_whenReloading_thenPreserveMonetaryScale() {
        val ordemServico = repository.salvar(OrdemServicoFixtures.ordemServico())
        sincronizar()

        val item = repository.buscarPorId(ordemServico.id)!!.orcamento.itens.first()

        assertEquals(OrdemServicoFixtures.PECA_PRECO, item.valorUnitario)
        assertEquals(OrdemServicoFixtures.QUANTIDADE_PECA, item.quantidade)
        assertEquals(BigDecimal("179.80"), item.subtotal)
    }

    @Test
    @DisplayName("Dado ordens de clientes distintos, quando buscar por cliente, então deve filtrar pelo cliente")
    fun givenOrdensFromDifferentClientes_whenFindingByCliente_thenFilterByCliente() {
        val outroCliente = ClienteRef.toUUID(OrdemServicoFixtures.ID_ALTERNATIVO)

        repository.salvar(OrdemServicoFixtures.ordemServico())
        repository.salvar(
            OrdemServicoFixtures
                .ordemServico(id = OrdemServicoFixtures.ID_ALTERNATIVO)
                .copy(clienteRef = outroCliente),
        )
        sincronizar()

        assertEquals(1, repository.buscarPorCliente(ClienteRef.toUUID(OrdemServicoFixtures.CLIENTE_ID)).size)
        assertEquals(1, repository.buscarPorCliente(outroCliente).size)
        assertTrue(repository.buscarPorCliente(ClienteRef.toUUID(OrdemServicoFixtures.PECA_ID)).isEmpty())
    }

    @Test
    @DisplayName("Dado ordens de veículos distintos, quando buscar por veículo, então deve filtrar pelo veículo")
    fun givenOrdensFromDifferentVeiculos_whenFindingByVeiculo_thenFilterByVeiculo() {
        val outroVeiculo = VeiculoRef.toUUID(OrdemServicoFixtures.ID_ALTERNATIVO)

        repository.salvar(OrdemServicoFixtures.ordemServico())
        repository.salvar(
            OrdemServicoFixtures
                .ordemServico(id = OrdemServicoFixtures.ID_ALTERNATIVO)
                .copy(veiculoRef = outroVeiculo),
        )
        sincronizar()

        assertEquals(1, repository.buscarPorVeiculo(VeiculoRef.toUUID(OrdemServicoFixtures.VEICULO_ID)).size)
        assertEquals(1, repository.buscarPorVeiculo(outroVeiculo).size)
    }

    @Test
    @DisplayName("Dado ordens em status diferentes, quando buscar por status, então deve filtrar pelo status")
    fun givenOrdensInDifferentStatuses_whenFindingByStatus_thenFilterByStatus() {
        repository.salvar(OrdemServicoFixtures.ordemServico())
        repository.salvar(
            OrdemServicoFixtures.ordemServico(
                id = OrdemServicoFixtures.ID_ALTERNATIVO,
                status = OrdemServicoStatus.EM_DIAGNOSTICO,
            ),
        )
        sincronizar()

        assertEquals(1, repository.buscarPorStatus(OrdemServicoStatus.RECEBIDA).size)
        assertEquals(1, repository.buscarPorStatus(OrdemServicoStatus.EM_DIAGNOSTICO).size)
        assertTrue(repository.buscarPorStatus(OrdemServicoStatus.ENTREGUE).isEmpty())
    }

    @Test
    @DisplayName("Dado ordens em datas distintas, quando listar por intervalo, então deve respeitar os limites")
    fun givenOrdensOnDifferentDates_whenListingByInterval_thenRespectBoundaries() {
        val dataAnterior = OrdemServicoFixtures.DATA_ABERTURA.minusSeconds(86_400)

        repository.salvar(OrdemServicoFixtures.ordemServico())
        repository.salvar(
            OrdemServicoFixtures.ordemServico(
                id = OrdemServicoFixtures.ID_ALTERNATIVO,
                dataAbertura = dataAnterior,
            ),
        )
        sincronizar()

        val noIntervalo =
            repository.listarPorDataAberturaEntre(
                inicio = OrdemServicoFixtures.DATA_ABERTURA.minusSeconds(60),
                fim = OrdemServicoFixtures.DATA_ABERTURA.plusSeconds(60),
            )

        assertEquals(1, noIntervalo.size)
        assertEquals(OrdemServicoFixtures.ID, noIntervalo.first().id.toString())
        assertEquals(2, repository.listarPorDataAberturaEntre(dataAnterior, OrdemServicoFixtures.DATA_ABERTURA).size)
    }

    @Test
    @DisplayName("Dado ordens cadastradas, quando listar todas, então deve retornar todas com seus itens")
    fun givenRegisteredOrdens_whenListingAll_thenReturnAllWithItems() {
        repository.salvar(OrdemServicoFixtures.ordemServico())
        repository.salvar(OrdemServicoFixtures.ordemServico(id = OrdemServicoFixtures.ID_ALTERNATIVO))
        sincronizar()

        val todas = repository.listarTodos()

        assertEquals(2, todas.size)
        assertTrue(todas.all { it.orcamento.itens.size == 2 })
    }

    @Test
    @DisplayName("Dado nenhuma ordem cadastrada, quando listar todas, então deve retornar lista vazia")
    fun givenNoRegisteredOrdens_whenListingAll_thenReturnEmptyList() {
        assertEquals(emptyList<OrdemServico>(), repository.listarTodos())
    }

    @Test
    @DisplayName("Dado uma ordem persistida, quando deletar, então deve remover a ordem e os itens em cascata")
    fun givenPersistedOrdem_whenDeleting_thenRemoveOrdemAndItemsInCascade() {
        val ordemServico = repository.salvar(OrdemServicoFixtures.ordemServico())
        sincronizar()

        assertTrue(repository.deletar(ordemServico.id))
        sincronizar()

        assertEquals(0, jpaRepository.count())
        assertEquals(0, contarItens())
        assertNull(repository.buscarPorId(ordemServico.id))
    }

    @Test
    @DisplayName("Dado um id inexistente, quando deletar, então deve retornar falso sem afetar os registros")
    fun givenNonExistentId_whenDeleting_thenReturnFalseWithoutAffectingRecords() {
        repository.salvar(OrdemServicoFixtures.ordemServico())
        sincronizar()

        assertFalse(repository.deletar(OrdemServicoId.generate()))
        sincronizar()

        assertEquals(1, jpaRepository.count())
    }

    @Test
    @DisplayName("Dado um id inexistente, quando buscar por id, então deve retornar nulo")
    fun givenNonExistentId_whenFindingById_thenReturnNull() {
        assertNull(repository.buscarPorId(OrdemServicoId.generate()))
    }

    private fun sincronizar() {
        entityManager.flush()
        entityManager.clear()
    }

    private fun contarItens(): Long = contar("SELECT count(*) FROM itens_orcamento")

    private fun contarItensOrfaos(): Long =
        contar("SELECT count(*) FROM itens_orcamento WHERE ordem_servico_id IS NULL")

    private fun contar(sql: String): Long = (
        entityManager.entityManager
            .createNativeQuery(sql)
            .singleResult as Number
        ).toLong()

    companion object {
        @JvmStatic
        @ServiceConnection
        val postgres: PostgreSQLContainer<*> = PostgreSQLContainer("postgres:16-alpine")
    }
}
