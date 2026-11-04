package br.com.fiap.oficina.ordemservico.application.usecase

import br.com.fiap.oficina.ordemservico.OrdemServicoFixtures
import br.com.fiap.oficina.ordemservico.application.dto.AlterarStatusRequest
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
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

@DisplayName("Use case - Alterar status da Ordem de Serviço")
class AlterarStatusOrdemServicoUseCaseTest {
    private val repositoryMock = mockk<OrdemServicoRepository>()
    private val usecase =
        AlterarStatusOrdemServicoUseCaseImpl(repositoryMock, OrdemServicoMapper(ItemOrcamentoMapper()))

    @Test
    @DisplayName("Dado uma transição permitida, quando alterar o status, então deve persistir o novo status")
    fun givenAllowedTransition_whenChangingStatus_thenPersistNewStatus() {
        every { repositoryMock.buscarPorId(any()) } returns OrdemServicoFixtures.ordemServico()
        every { repositoryMock.salvar(any()) } answers { firstArg() }

        val response =
            usecase.executar(
                id = OrdemServicoFixtures.ID,
                request = AlterarStatusRequest(status = OrdemServicoStatus.EM_DIAGNOSTICO),
            )

        assertEquals(OrdemServicoStatus.EM_DIAGNOSTICO, response.status)
        assertNull(response.dataInicioExecucao)

        verify(exactly = 1) { repositoryMock.salvar(any()) }
    }

    @Test
    @DisplayName("Dado o cancelamento a partir da aprovação, quando alterar o status, então deve persistir cancelada")
    fun givenCancellationFromApproval_whenChangingStatus_thenPersistCancelled() {
        every { repositoryMock.buscarPorId(any()) } returns
            OrdemServicoFixtures.ordemServico(status = OrdemServicoStatus.AGUARDANDO_APROVACAO)
        every { repositoryMock.salvar(any()) } answers { firstArg() }

        val response =
            usecase.executar(
                id = OrdemServicoFixtures.ID,
                request = AlterarStatusRequest(status = OrdemServicoStatus.CANCELADA),
            )

        assertEquals(OrdemServicoStatus.CANCELADA, response.status)
    }

    @Test
    @DisplayName("Dado a entrada em execução, quando alterar o status, então deve marcar o início da execução")
    fun givenEntryInExecution_whenChangingStatus_thenStampExecutionStart() {
        val capturada = slot<OrdemServico>()

        every { repositoryMock.buscarPorId(any()) } returns
            OrdemServicoFixtures.ordemServico(status = OrdemServicoStatus.AGUARDANDO_APROVACAO)
        every { repositoryMock.salvar(capture(capturada)) } answers { capturada.captured }

        usecase.executar(
            id = OrdemServicoFixtures.ID,
            request = AlterarStatusRequest(status = OrdemServicoStatus.EM_EXECUCAO),
        )

        assertNotNull(capturada.captured.dataInicioExecucao)
        assertNull(capturada.captured.dataFinalizacao)
    }

    @Test
    @DisplayName("Dado uma transição não permitida, quando alterar o status, então deve lançar exceção")
    fun givenForbiddenTransition_whenChangingStatus_thenThrowException() {
        every { repositoryMock.buscarPorId(any()) } returns OrdemServicoFixtures.ordemServico()

        val exception =
            assertFailsWith<BusinessRuleException> {
                usecase.executar(
                    id = OrdemServicoFixtures.ID,
                    request = AlterarStatusRequest(status = OrdemServicoStatus.ENTREGUE),
                )
            }

        assertEquals(
            "Transição inválida de 'RECEBIDA' para 'ENTREGUE'. " +
                "Transições permitidas a partir de 'RECEBIDA': [EM_DIAGNOSTICO]",
            exception.message,
        )

        verify(exactly = 0) { repositoryMock.salvar(any()) }
    }

    @Test
    @DisplayName("Dado uma ordem em status final, quando alterar o status, então deve lançar exceção")
    fun givenOrdemInFinalStatus_whenChangingStatus_thenThrowException() {
        every { repositoryMock.buscarPorId(any()) } returns
            OrdemServicoFixtures.ordemServico(status = OrdemServicoStatus.ENTREGUE)

        assertFailsWith<BusinessRuleException> {
            usecase.executar(
                id = OrdemServicoFixtures.ID,
                request = AlterarStatusRequest(status = OrdemServicoStatus.EM_EXECUCAO),
            )
        }

        verify(exactly = 0) { repositoryMock.salvar(any()) }
    }

    @Test
    @DisplayName("Dado um id inexistente, quando alterar o status, então deve lançar exceção")
    fun givenNonExistentId_whenChangingStatus_thenThrowException() {
        every { repositoryMock.buscarPorId(any()) } returns null

        val exception =
            assertFailsWith<EntityNotFoundException> {
                usecase.executar(
                    id = OrdemServicoFixtures.ID,
                    request = AlterarStatusRequest(status = OrdemServicoStatus.EM_DIAGNOSTICO),
                )
            }

        assertEquals("Ordem de serviço não encontrada, id: ${OrdemServicoFixtures.ID}", exception.message)
    }

    @Test
    @DisplayName("Dado um id inválido, quando alterar o status, então deve lançar exceção")
    fun givenInvalidId_whenChangingStatus_thenThrowException() {
        assertFailsWith<IllegalArgumentException> {
            usecase.executar(
                id = OrdemServicoFixtures.ID_INVALIDO,
                request = AlterarStatusRequest(status = OrdemServicoStatus.EM_DIAGNOSTICO),
            )
        }

        verify(exactly = 0) { repositoryMock.buscarPorId(any()) }
    }
}
