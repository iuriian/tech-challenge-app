package br.com.fiap.oficina.ordemservico.application.usecase

import br.com.fiap.oficina.ordemservico.OrdemServicoFixtures
import br.com.fiap.oficina.ordemservico.application.mapper.ItemOrcamentoMapper
import br.com.fiap.oficina.ordemservico.application.mapper.OrdemServicoMapper
import br.com.fiap.oficina.ordemservico.application.resolver.OrcamentoResolver
import br.com.fiap.oficina.ordemservico.application.resolver.ReferenciaResolver
import br.com.fiap.oficina.ordemservico.domain.OrdemServico
import br.com.fiap.oficina.ordemservico.domain.OrdemServicoRepository
import br.com.fiap.oficina.ordemservico.domain.OrdemServicoStatus
import br.com.fiap.oficina.ordemservico.domain.SequenciaOrdemServico
import br.com.fiap.oficina.ordemservico.domain.port.cliente.ConsultarCliente
import br.com.fiap.oficina.ordemservico.domain.port.funcionario.ConsultarFuncionario
import br.com.fiap.oficina.ordemservico.domain.port.peca.ConsultarPeca
import br.com.fiap.oficina.ordemservico.domain.port.servico.ConsultarServico
import br.com.fiap.oficina.ordemservico.domain.port.veiculo.ConsultarVeiculo
import br.com.fiap.oficina.shared.domain.exception.BusinessRuleException
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

@DisplayName("Use case - Criar Ordem de Serviço")
class CriarOrdemServicoUseCaseTest {
    private val repositoryMock = mockk<OrdemServicoRepository>()
    private val sequenciaMock = mockk<SequenciaOrdemServico>()
    private val consultarClienteMock = mockk<ConsultarCliente>()
    private val consultarFuncionarioMock = mockk<ConsultarFuncionario>()
    private val consultarVeiculoMock = mockk<ConsultarVeiculo>()
    private val consultarPecaMock = mockk<ConsultarPeca>()
    private val consultarServicoMock = mockk<ConsultarServico>()

    private val usecase =
        CriarOrdemServicoUseCaseImpl(
            repository = repositoryMock,
            sequencia = sequenciaMock,
            referenciaResolver =
            ReferenciaResolver(
                consultarClienteMock,
                consultarFuncionarioMock,
                consultarVeiculoMock,
            ),
            orcamentoResolver = OrcamentoResolver(consultarPecaMock, consultarServicoMock),
            mapper = OrdemServicoMapper(ItemOrcamentoMapper()),
        )

    @Test
    @DisplayName("Dado um request válido, quando criar, então deve persistir com número gerado e status recebida")
    fun givenValidRequest_whenCreating_thenPersistWithGeneratedNumberAndReceivedStatus() {
        dependenciasResolvidas()

        val capturada = slot<OrdemServico>()
        every { repositoryMock.salvar(capture(capturada)) } answers { capturada.captured }

        val response = usecase.executar(OrdemServicoFixtures.request())

        assertEquals(OrdemServicoFixtures.DESCRICAO, response.descricao)
        assertEquals(OrdemServicoStatus.RECEBIDA, response.status)
        assertEquals(OrdemServicoFixtures.CLIENTE_ID, response.clienteId)
        assertEquals(OrdemServicoFixtures.FUNCIONARIO_ID, response.funcionarioId)
        assertEquals(OrdemServicoFixtures.VEICULO_ID, response.veiculoId)
        assertEquals(1, response.itens.size)
        assertNotNull(response.dataAbertura)
        assertNull(response.dataInicioExecucao)

        assertEquals(OrdemServicoFixtures.numero(), capturada.captured.osNumber)

        verify(exactly = 1) { sequenciaMock.proximoValor() }
        verify(exactly = 1) { repositoryMock.salvar(any()) }
    }

    @Test
    @DisplayName("Dado um request sem itens, quando criar, então deve persistir com orçamento vazio")
    fun givenRequestWithoutItems_whenCreating_thenPersistWithEmptyBudget() {
        dependenciasResolvidas()
        every { repositoryMock.salvar(any()) } answers { firstArg() }

        val response = usecase.executar(OrdemServicoFixtures.request(itens = emptyList()))

        assertEquals(0, response.itens.size)

        verify(exactly = 0) { consultarPecaMock.buscarPorId(any()) }
    }

    @Test
    @DisplayName("Dado um cliente inexistente, quando criar, então deve lançar exceção e não persistir")
    fun givenNonExistentCliente_whenCreating_thenThrowExceptionAndDoNotPersist() {
        dependenciasResolvidas()
        every { consultarClienteMock.buscarPorId(any()) } returns null

        assertFailsWith<BusinessRuleException> { usecase.executar(OrdemServicoFixtures.request()) }

        verify(exactly = 0) { repositoryMock.salvar(any()) }
        verify(exactly = 0) { sequenciaMock.proximoValor() }
    }

    @Test
    @DisplayName("Dado uma peça inativa, quando criar, então deve lançar exceção e não persistir")
    fun givenInactivePeca_whenCreating_thenThrowExceptionAndDoNotPersist() {
        dependenciasResolvidas()
        every { consultarPecaMock.buscarPorId(any()) } returns OrdemServicoFixtures.dadosPeca(ativo = false)

        assertFailsWith<BusinessRuleException> { usecase.executar(OrdemServicoFixtures.request()) }

        verify(exactly = 0) { repositoryMock.salvar(any()) }
    }

    @Test
    @DisplayName("Dado um request com descrição em branco, quando criar, então deve lançar exceção e não persistir")
    fun givenRequestWithBlankDescription_whenCreating_thenThrowExceptionAndDoNotPersist() {
        dependenciasResolvidas()

        assertFailsWith<IllegalArgumentException> {
            usecase.executar(OrdemServicoFixtures.request(descricao = " "))
        }

        verify(exactly = 0) { repositoryMock.salvar(any()) }
    }

    private fun dependenciasResolvidas() {
        every { consultarClienteMock.buscarPorId(any()) } returns OrdemServicoFixtures.dadosCliente()
        every { consultarFuncionarioMock.buscarPorId(any()) } returns OrdemServicoFixtures.dadosFuncionario()
        every { consultarVeiculoMock.buscarPorId(any()) } returns OrdemServicoFixtures.dadosVeiculo()
        every { consultarPecaMock.buscarPorId(any()) } returns OrdemServicoFixtures.dadosPeca()
        every { consultarServicoMock.buscarPorId(any()) } returns OrdemServicoFixtures.dadosServico()
        every { sequenciaMock.proximoValor() } returns OrdemServicoFixtures.SEQUENCIAL
    }
}
