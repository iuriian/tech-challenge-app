package br.com.fiap.oficina.ordemservico.application.usecase

import br.com.fiap.oficina.ordemservico.OrdemServicoFixtures
import br.com.fiap.oficina.ordemservico.domain.OrdemServicoId
import br.com.fiap.oficina.ordemservico.domain.OrdemServicoRepository
import br.com.fiap.oficina.shared.domain.exception.EntityNotFoundException
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

@DisplayName("Use case - Deletar Ordem de Serviço")
class DeletarOrdemServicoUseCaseTest {
    private val repositoryMock = mockk<OrdemServicoRepository>()
    private val usecase = DeletarOrdemServicoUseCaseImpl(repositoryMock)

    @Test
    @DisplayName("Dado um id existente, quando deletar, então deve remover a ordem de serviço")
    fun givenExistingId_whenDeleting_thenRemoveOrdemServico() {
        val id = OrdemServicoId.toUUID(OrdemServicoFixtures.ID)

        every { repositoryMock.deletar(id) } returns true

        usecase.executar(OrdemServicoFixtures.ID)

        verify(exactly = 1) { repositoryMock.deletar(id) }
    }

    @Test
    @DisplayName("Dado um id inexistente, quando deletar, então deve lançar exceção")
    fun givenNonExistentId_whenDeleting_thenThrowException() {
        every { repositoryMock.deletar(any()) } returns false

        val exception = assertFailsWith<EntityNotFoundException> { usecase.executar(OrdemServicoFixtures.ID) }

        assertEquals("Ordem de serviço não encontrada com o ID: ${OrdemServicoFixtures.ID}", exception.message)
    }

    @Test
    @DisplayName("Dado um id inválido, quando deletar, então deve lançar exceção sem acionar o repositório")
    fun givenInvalidId_whenDeleting_thenThrowExceptionWithoutCallingRepository() {
        assertFailsWith<IllegalArgumentException> { usecase.executar(OrdemServicoFixtures.ID_INVALIDO) }

        verify(exactly = 0) { repositoryMock.deletar(any()) }
    }
}
