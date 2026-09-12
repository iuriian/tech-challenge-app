package br.com.fiap.oficina.funcionario.application.usecase

import br.com.fiap.oficina.funcionario.application.dto.FuncionarioRequest
import br.com.fiap.oficina.funcionario.application.mapper.FuncionarioMapper
import br.com.fiap.oficina.funcionario.domain.Funcionario
import br.com.fiap.oficina.funcionario.domain.FuncionarioException
import br.com.fiap.oficina.funcionario.domain.FuncionarioRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

@DisplayName("Use case - Atualizar Funcionário")
class AtualizarFuncionarioUseCaseTest {
    private val repositoryMock = mockk<FuncionarioRepository>(relaxed = true)
    private val mapperMock = mockk<FuncionarioMapper>(relaxed = true)
    private val usecase = AtualizarFuncionarioUseCaseImpl(repositoryMock, mapperMock)

    @Test
    @DisplayName("Dado dados válidos, quando atualizar funcionario, então deve retornar funcionario atualizado")
    fun givenValidData_whenUpdateFuncionario_thenReturnUpdatedFuncionario() {
        val request =
            FuncionarioRequest(
                id = "00000000-0000-0000-0000-000000000100",
                nome = "Test atualizado",
                cargo = "MECANICO",
                cpf = "01234567891",
            )

        val funcionario =
            Funcionario.reconstruir(
                id = "00000000-0000-0000-0000-000000000100",
                nome = "Test",
                cargo = "ATENDENTE",
                cpf = "01234567890",
            )

        every { repositoryMock.buscarPorId(any()) } returns funcionario
        every { repositoryMock.buscarPorCpf(any()) } returns funcionario

        val result = usecase.executar(request)

        assertNotNull(result)

        verify(exactly = 1) { repositoryMock.buscarPorId(any()) }
        verify(exactly = 1) { repositoryMock.buscarPorCpf(any()) }
        verify(exactly = 1) { repositoryMock.salvar(any()) }
    }

    @Test
    @DisplayName("Dado dados sem id, quando atualizar funcionario, então deve lançar exceção")
    fun givenDataNotContainId_whenUpdateFuncionario_thenThrowException() {
        val request =
            FuncionarioRequest(
                nome = "Test",
                cargo = "MECANICO",
                cpf = "01234567891",
            )

        val exception =
            assertFailsWith<FuncionarioException> {
                usecase.executar(request)
            }

        assertEquals("Id é obrigatório!", exception.message)

        verify(exactly = 0) { repositoryMock.buscarPorId(any()) }
    }

    @Test
    @DisplayName("Dado dados com id inexistente, quando atualizar funcionario, então deve lançar exceção")
    fun givenDataWithNonExistentId_whenUpdateFuncionario_thenThrowException() {
        val request =
            FuncionarioRequest(
                id = "00000000-0000-0000-0000-000000000100",
                nome = "Test",
                cargo = "MECANICO",
                cpf = "01234567891",
            )

        every { repositoryMock.buscarPorId(any()) } returns null

        val exception =
            assertFailsWith<FuncionarioException> {
                usecase.executar(request)
            }

        assertEquals("Funcionário não encontrado!", exception.message)

        verify(exactly = 1) { repositoryMock.buscarPorId(any()) }
    }

    @Test
    @DisplayName("Dado dados com cpf já existente, quando atualizar funcionario, então deve lançar exceção")
    fun givenDataWithExistingCpf_whenUpdateFuncionario_thenThrowException() {
        val request =
            FuncionarioRequest(
                id = "00000000-0000-0000-0000-000000000100",
                nome = "Test atualizado",
                cargo = "MECANICO",
                cpf = "01234567891",
            )

        val funcionario =
            Funcionario.reconstruir(
                id = "00000000-0000-0000-0000-000000000100",
                nome = "Test",
                cargo = "ATENDENTE",
                cpf = "01234567890",
            )

        val funcionarioExistente =
            Funcionario.reconstruir(
                id = "00000000-0000-0000-0000-000000000200",
                nome = "Test Existente",
                cargo = "MECANICO",
                cpf = "01234567891",
            )

        every { repositoryMock.buscarPorId(any()) } returns funcionario
        every { repositoryMock.buscarPorCpf(any()) } returns funcionarioExistente

        val exception =
            assertFailsWith<FuncionarioException> {
                usecase.executar(request)
            }

        assertEquals("CPF já cadastrado!", exception.message)

        verify(exactly = 1) { repositoryMock.buscarPorId(any()) }
        verify(exactly = 1) { repositoryMock.buscarPorCpf(any()) }
    }
}
