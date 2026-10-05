package br.com.fiap.oficina.funcionario.application.usecase

import br.com.fiap.oficina.funcionario.domain.Funcionario
import br.com.fiap.oficina.funcionario.domain.FuncionarioException
import br.com.fiap.oficina.funcionario.domain.FuncionarioId
import br.com.fiap.oficina.funcionario.domain.FuncionarioRepository
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@DisplayName("Use case - Remover funcionário")
class RemoverFuncionarioUseCaseTest {
    private val repositoryMock = mockk<FuncionarioRepository>()
    private val usecase = RemoverFuncionarioUseCaseImpl(repositoryMock)

    @Test
    @DisplayName("Dado que o funcionário existe, quando remover funcionário, então o funcionário é removido")
    fun givenFuncionarioExists_whenRemoverFuncionario_thenFuncionarioIsRemoved() {
        val id = "00000000-0000-0000-0000-000000000100"
        val funcionarioId = FuncionarioId.toUUID(id)

        val funcionario =
            Funcionario.reconstruir(
                id = id,
                nome = "Nome",
                cpf = "01234567890",
                cargo = "ATENDENTE",
            )

        every { repositoryMock.buscarPorId(funcionarioId) } returns funcionario
        every { repositoryMock.deletar(funcionarioId) } just runs

        usecase.executar(id)

        verify(exactly = 1) { repositoryMock.buscarPorId(funcionarioId) }
        verify(exactly = 1) { repositoryMock.deletar(funcionarioId) }
    }

    @Test
    @DisplayName("Dado que o funcionário não existe, quando remover funcionário, então lançar exceção")
    fun givenFuncionarioNotExists_whenRemoverFuncionario_thenThrowException() {
        val id = "00000000-0000-0000-0000-000000000100"
        val funcionarioId = FuncionarioId.toUUID(id)

        every { repositoryMock.buscarPorId(funcionarioId) } returns null

        val exception =
            assertFailsWith<FuncionarioException> {
                usecase.executar(id)
            }

        assertEquals("Funcionário não encontrado com o ID: $id", exception.message)

        verify(exactly = 1) { repositoryMock.buscarPorId(funcionarioId) }
        verify(exactly = 0) { repositoryMock.deletar(any()) }
    }

    @Test
    @DisplayName("Dado id em formato inválido, quando remover funcionário, então lançar exceção")
    fun givenInvalidIdFormat_whenRemoverFuncionario_thenThrowException() {
        val id = "id-invalido"

        assertFailsWith<IllegalArgumentException> {
            usecase.executar(id)
        }

        verify(exactly = 0) { repositoryMock.buscarPorId(any()) }
        verify(exactly = 0) { repositoryMock.deletar(any()) }
    }
}
