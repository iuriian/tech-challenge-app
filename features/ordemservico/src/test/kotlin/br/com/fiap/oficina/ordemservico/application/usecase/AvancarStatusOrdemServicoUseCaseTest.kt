package br.com.fiap.oficina.ordemservico.application.usecase

import br.com.fiap.oficina.ordemservico.OrdemServicoFixtures
import br.com.fiap.oficina.ordemservico.application.mapper.ItemOrcamentoMapper
import br.com.fiap.oficina.ordemservico.application.mapper.OrdemServicoMapper
import br.com.fiap.oficina.ordemservico.domain.OrdemServico
import br.com.fiap.oficina.ordemservico.domain.OrdemServicoRepository
import br.com.fiap.oficina.ordemservico.domain.OrdemServicoStatus
import br.com.fiap.oficina.shared.domain.exception.BusinessRuleException
import br.com.fiap.oficina.shared.domain.exception.EntityNotFoundException
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import kotlin.test.assertFailsWith

@DisplayName("Use case - Avançar status da Ordem de Serviço")
class AvancarStatusOrdemServicoUseCaseTest {
    private val repositoryMock = mockk<OrdemServicoRepository>()
    private val usecase =
        AvancarStatusOrdemServicoUseCaseImpl(repositoryMock, OrdemServicoMapper(ItemOrcamentoMapper()))

    @Test
    @DisplayName("Dado uma ordem recebida, quando avançar, então deve persistir em diagnóstico")
    fun givenReceivedOrdem_whenAdvancing_thenPersistInDiagnosis() {
        every { repositoryMock.buscarPorId(any()) } returns OrdemServicoFixtures.ordemServico()
        every { repositoryMock.salvar(any()) } answers { firstArg() }

        val response = usecase.executar(OrdemServicoFixtures.ID)

        assertEquals(OrdemServicoStatus.EM_DIAGNOSTICO, response.status)

        verify(exactly = 1) { repositoryMock.salvar(any()) }
    }

    @Test
    @DisplayName("Dado uma ordem aguardando aprovação, quando avançar, então deve marcar o início da execução")
    fun givenOrdemAwaitingApproval_whenAdvancing_thenStampExecutionStart() {
        val capturada = slot<OrdemServico>()

        every { repositoryMock.buscarPorId(any()) } returns
            OrdemServicoFixtures.ordemServico(status = OrdemServicoStatus.AGUARDANDO_APROVACAO)
        every { repositoryMock.salvar(capture(capturada)) } answers { capturada.captured }

        val response = usecase.executar(OrdemServicoFixtures.ID)

        assertEquals(OrdemServicoStatus.EM_EXECUCAO, response.status)
        assertNotNull(response.dataInicioExecucao)
        assertNotNull(capturada.captured.dataInicioExecucao)
    }

    @ParameterizedTest(name = "status {0}")
    @EnumSource(value = OrdemServicoStatus::class, names = ["ENTREGUE", "CANCELADA"])
    @DisplayName("Dado uma ordem em status final, quando avançar, então deve lançar exceção e não persistir")
    fun givenOrdemInFinalStatus_whenAdvancing_thenThrowExceptionAndDoNotPersist(status: OrdemServicoStatus) {
        every { repositoryMock.buscarPorId(any()) } returns OrdemServicoFixtures.ordemServico(status = status)

        val exception =
            assertFailsWith<BusinessRuleException> { usecase.executar(OrdemServicoFixtures.ID) }

        assertEquals(
            "Ordem de serviço no status '$status' é um estado final e não pode ser avançada",
            exception.message,
        )

        verify(exactly = 0) { repositoryMock.salvar(any()) }
    }

    @Test
    @DisplayName("Dado um id inexistente, quando avançar, então deve lançar exceção e não persistir")
    fun givenNonExistentId_whenAdvancing_thenThrowExceptionAndDoNotPersist() {
        every { repositoryMock.buscarPorId(any()) } returns null

        val exception =
            assertFailsWith<EntityNotFoundException> { usecase.executar(OrdemServicoFixtures.ID) }

        assertEquals("Ordem de serviço não encontrada, id: ${OrdemServicoFixtures.ID}", exception.message)

        verify(exactly = 0) { repositoryMock.salvar(any()) }
    }

    @Test
    @DisplayName("Dado um id inválido, quando avançar, então deve lançar exceção sem consultar o repositório")
    fun givenInvalidId_whenAdvancing_thenThrowExceptionWithoutQueryingRepository() {
        assertFailsWith<IllegalArgumentException> { usecase.executar(OrdemServicoFixtures.ID_INVALIDO) }

        verify(exactly = 0) { repositoryMock.buscarPorId(any()) }
    }
}
