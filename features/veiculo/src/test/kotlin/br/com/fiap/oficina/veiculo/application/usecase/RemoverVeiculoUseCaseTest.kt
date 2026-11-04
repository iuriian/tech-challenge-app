package br.com.fiap.oficina.veiculo.application.usecase

import br.com.fiap.oficina.shared.domain.exception.EntityNotFoundException
import br.com.fiap.oficina.veiculo.VeiculoFixtures
import br.com.fiap.oficina.veiculo.domain.VeiculoId
import br.com.fiap.oficina.veiculo.domain.VeiculoRepository
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

@DisplayName("Use case - Remover Veículo")
class RemoverVeiculoUseCaseTest {
    private val repositoryMock = mockk<VeiculoRepository>()
    private val usecase = RemoverVeiculoUseCaseImpl(repositoryMock)

    @Test
    @DisplayName("Dado um id existente, quando remover veículo, então deve delegar a exclusão ao repositório")
    fun givenExistingId_whenRemovingVeiculo_thenDelegateDeletionToRepository() {
        val id = VeiculoId.toUUID(VeiculoFixtures.ID_1)

        every { repositoryMock.buscarPorId(id) } returns VeiculoFixtures.veiculo()
        every { repositoryMock.deletar(id) } just runs

        usecase.executar(VeiculoFixtures.ID_1)

        verify(exactly = 1) { repositoryMock.buscarPorId(id) }
        verify(exactly = 1) { repositoryMock.deletar(id) }
    }

    @Test
    @DisplayName("Dado um id inexistente, quando remover veículo, então deve lançar exceção")
    fun givenNonExistentId_whenRemovingVeiculo_thenThrowException() {
        every { repositoryMock.buscarPorId(any()) } returns null

        val exception =
            assertFailsWith<EntityNotFoundException> {
                usecase.executar(VeiculoFixtures.ID_1)
            }

        assertEquals("Veículo não encontrado para o id: ${VeiculoFixtures.ID_1}", exception.message)

        verify(exactly = 0) { repositoryMock.deletar(any()) }
    }

    @Test
    @DisplayName("Dado um id inválido, quando remover veículo, então deve lançar exceção")
    fun givenInvalidId_whenRemovingVeiculo_thenThrowException() {
        assertFailsWith<IllegalArgumentException> { usecase.executar("nao-e-um-uuid") }

        verify(exactly = 0) { repositoryMock.buscarPorId(any()) }
        verify(exactly = 0) { repositoryMock.deletar(any()) }
    }
}
