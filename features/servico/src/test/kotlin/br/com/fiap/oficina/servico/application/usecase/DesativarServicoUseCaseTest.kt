package br.com.fiap.oficina.servico.application.usecase

import br.com.fiap.oficina.servico.ServicoFixtures
import br.com.fiap.oficina.servico.domain.Servico
import br.com.fiap.oficina.servico.domain.ServicoId
import br.com.fiap.oficina.servico.domain.ServicoRepository
import br.com.fiap.oficina.shared.domain.exception.EntityNotFoundException
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

@DisplayName("Use case - Desativar Serviço")
class DesativarServicoUseCaseTest {
    private val repositoryMock = mockk<ServicoRepository>()
    private val usecase = DesativarServicoUseCaseImpl(repositoryMock)

    @Test
    @DisplayName("Dado um serviço ativo, quando desativar, então deve persistir o serviço inativo")
    fun givenActiveServico_whenDeactivating_thenPersistInactiveServico() {
        val id = ServicoId.toUUID(ServicoFixtures.ID_1)
        val capturado = slot<Servico>()

        every { repositoryMock.buscarPorId(id) } returns ServicoFixtures.servico(ativo = true)
        every { repositoryMock.salvar(capture(capturado)) } answers { capturado.captured }

        usecase.executar(ServicoFixtures.ID_1)

        assertFalse(capturado.captured.ativo)
        assertEquals(ServicoFixtures.ID_1, capturado.captured.id.toString())

        verify(exactly = 1) { repositoryMock.salvar(any()) }
    }

    @Test
    @DisplayName("Dado um id inexistente, quando desativar, então deve lançar exceção e não persistir")
    fun givenNonExistentId_whenDeactivating_thenThrowExceptionAndDoNotPersist() {
        every { repositoryMock.buscarPorId(any()) } returns null

        val exception =
            assertFailsWith<EntityNotFoundException> {
                usecase.executar(ServicoFixtures.ID_1)
            }

        assertEquals("Serviço não encontrado com o id: ${ServicoFixtures.ID_1}", exception.message)

        verify(exactly = 0) { repositoryMock.salvar(any()) }
    }

    @Test
    @DisplayName("Dado um id inválido, quando desativar, então deve lançar exceção")
    fun givenInvalidId_whenDeactivating_thenThrowException() {
        assertFailsWith<IllegalArgumentException> { usecase.executar("nao-e-um-uuid") }

        verify(exactly = 0) { repositoryMock.buscarPorId(any()) }
    }
}
