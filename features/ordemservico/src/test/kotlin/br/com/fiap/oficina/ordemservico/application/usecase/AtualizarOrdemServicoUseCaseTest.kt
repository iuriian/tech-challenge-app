package br.com.fiap.oficina.ordemservico.application.usecase

import br.com.fiap.oficina.ordemservico.OrdemServicoFixtures
import br.com.fiap.oficina.ordemservico.application.mapper.ItemOrcamentoMapper
import br.com.fiap.oficina.ordemservico.application.mapper.OrdemServicoMapper
import br.com.fiap.oficina.ordemservico.application.resolver.OrcamentoResolver
import br.com.fiap.oficina.ordemservico.application.resolver.ReferenciaResolver
import br.com.fiap.oficina.ordemservico.domain.OrdemServico
import br.com.fiap.oficina.ordemservico.domain.OrdemServicoId
import br.com.fiap.oficina.ordemservico.domain.OrdemServicoRepository
import br.com.fiap.oficina.ordemservico.domain.OrdemServicoStatus
import br.com.fiap.oficina.ordemservico.domain.port.cliente.ConsultarCliente
import br.com.fiap.oficina.ordemservico.domain.port.funcionario.ConsultarFuncionario
import br.com.fiap.oficina.ordemservico.domain.port.peca.ConsultarPeca
import br.com.fiap.oficina.ordemservico.domain.port.servico.ConsultarServico
import br.com.fiap.oficina.ordemservico.domain.port.veiculo.ConsultarVeiculo
import br.com.fiap.oficina.shared.domain.exception.BusinessRuleException
import br.com.fiap.oficina.shared.domain.exception.EntityNotFoundException
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

@DisplayName("Use case - Atualizar Ordem de Serviço")
class AtualizarOrdemServicoUseCaseTest {
    private val repositoryMock = mockk<OrdemServicoRepository>()
    private val consultarClienteMock = mockk<ConsultarCliente>()
    private val consultarFuncionarioMock = mockk<ConsultarFuncionario>()
    private val consultarVeiculoMock = mockk<ConsultarVeiculo>()
    private val consultarPecaMock = mockk<ConsultarPeca>()
    private val consultarServicoMock = mockk<ConsultarServico>()

    private val usecase =
        AtualizarOrdemServicoUseCaseImpl(
            repository = repositoryMock,
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
    @DisplayName("Dado uma ordem editável, quando atualizar, então deve persistir preservando a identidade")
    fun givenEditableOrdem_whenUpdating_thenPersistKeepingIdentity() {
        dependenciasResolvidas()

        val original = OrdemServicoFixtures.ordemServico()
        every { repositoryMock.buscarPorId(OrdemServicoId.toUUID(OrdemServicoFixtures.ID)) } returns original

        val capturada = slot<OrdemServico>()
        every { repositoryMock.salvar(capture(capturada)) } answers { capturada.captured }

        val response =
            usecase.executar(
                id = OrdemServicoFixtures.ID,
                request = OrdemServicoFixtures.request(descricao = OrdemServicoFixtures.DESCRICAO_ALTERNATIVA),
            )

        assertEquals(OrdemServicoFixtures.DESCRICAO_ALTERNATIVA, response.descricao)
        assertEquals(OrdemServicoFixtures.ID, response.id)
        assertEquals(1, response.itens.size)
        assertEquals(original.osNumber, capturada.captured.osNumber)
        assertEquals(original.dataAbertura, capturada.captured.dataAbertura)
        assertEquals(OrdemServicoStatus.RECEBIDA, capturada.captured.status)

        verify(exactly = 1) { repositoryMock.salvar(any()) }
    }

    @Test
    @DisplayName("Dado um id inexistente, quando atualizar, então deve lançar exceção e não persistir")
    fun givenNonExistentId_whenUpdating_thenThrowExceptionAndDoNotPersist() {
        every { repositoryMock.buscarPorId(any()) } returns null

        val exception =
            assertFailsWith<EntityNotFoundException> {
                usecase.executar(OrdemServicoFixtures.ID, OrdemServicoFixtures.request())
            }

        assertEquals("Ordem de serviço não encontrada, id: ${OrdemServicoFixtures.ID}", exception.message)

        verify(exactly = 0) { repositoryMock.salvar(any()) }
        verify(exactly = 0) { consultarClienteMock.buscarPorId(any()) }
    }

    @Test
    @DisplayName("Dado uma ordem em execução, quando atualizar, então deve lançar exceção e não persistir")
    fun givenOrdemInExecution_whenUpdating_thenThrowExceptionAndDoNotPersist() {
        every { repositoryMock.buscarPorId(any()) } returns
            OrdemServicoFixtures.ordemServico(status = OrdemServicoStatus.EM_EXECUCAO)

        val exception =
            assertFailsWith<BusinessRuleException> {
                usecase.executar(OrdemServicoFixtures.ID, OrdemServicoFixtures.request())
            }

        assertEquals(
            "Ordem de serviço no status 'EM_EXECUCAO' não pode ser atualizada",
            exception.message,
        )

        verify(exactly = 0) { repositoryMock.salvar(any()) }
    }

    @Test
    @DisplayName("Dado um veículo inexistente, quando atualizar, então deve lançar exceção e não persistir")
    fun givenNonExistentVeiculo_whenUpdating_thenThrowExceptionAndDoNotPersist() {
        dependenciasResolvidas()
        every { repositoryMock.buscarPorId(any()) } returns OrdemServicoFixtures.ordemServico()
        every { consultarVeiculoMock.buscarPorId(any()) } returns null

        assertFailsWith<BusinessRuleException> {
            usecase.executar(OrdemServicoFixtures.ID, OrdemServicoFixtures.request())
        }

        verify(exactly = 0) { repositoryMock.salvar(any()) }
    }

    @Test
    @DisplayName("Dado um id inválido, quando atualizar, então deve lançar exceção sem consultar o repositório")
    fun givenInvalidId_whenUpdating_thenThrowExceptionWithoutQueryingRepository() {
        assertFailsWith<IllegalArgumentException> {
            usecase.executar(OrdemServicoFixtures.ID_INVALIDO, OrdemServicoFixtures.request())
        }

        verify(exactly = 0) { repositoryMock.buscarPorId(any()) }
    }

    private fun dependenciasResolvidas() {
        every { consultarClienteMock.buscarPorId(any()) } returns OrdemServicoFixtures.dadosCliente()
        every { consultarFuncionarioMock.buscarPorId(any()) } returns OrdemServicoFixtures.dadosFuncionario()
        every { consultarVeiculoMock.buscarPorId(any()) } returns OrdemServicoFixtures.dadosVeiculo()
        every { consultarPecaMock.buscarPorId(any()) } returns OrdemServicoFixtures.dadosPeca()
        every { consultarServicoMock.buscarPorId(any()) } returns OrdemServicoFixtures.dadosServico()
    }
}
