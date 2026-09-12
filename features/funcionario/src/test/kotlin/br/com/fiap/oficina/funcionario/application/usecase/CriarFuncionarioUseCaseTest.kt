package br.com.fiap.oficina.funcionario.application.usecase

import br.com.fiap.oficina.funcionario.application.dto.FuncionarioRequest
import br.com.fiap.oficina.funcionario.application.dto.FuncionarioResponse
import br.com.fiap.oficina.funcionario.application.mapper.FuncionarioMapper
import br.com.fiap.oficina.funcionario.domain.Funcionario
import br.com.fiap.oficina.funcionario.domain.FuncionarioException
import br.com.fiap.oficina.funcionario.domain.FuncionarioRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

@DisplayName("Use case - Criar Funcionario")
class CriarFuncionarioUseCaseTest {
    private val repositoryMock = mockk<FuncionarioRepository>()
    private val mapperMock = mockk<FuncionarioMapper>()
    private val useCase = CriarFuncionarioUseCaseImpl(repositoryMock, mapperMock)

    @Test
    @DisplayName("Dado dados válidos, quando criar funcionário, então deve ter sucesso")
    fun givenValidData_whenCreatingFuncionario_thenShouldSucceed() {
        val request =
            FuncionarioRequest(
                nome = "João Silva",
                cargo = "MECANICO",
                cpf = "12345678900",
            )

        val funcionario =
            Funcionario.criar(
                nome = request.nome,
                cargo = request.cargo,
                cpf = request.cpf,
            )

        val response =
            FuncionarioResponse(
                id = funcionario.id.value.toString(),
                nome = funcionario.nome,
                cargoDescricao = funcionario.cargo.descricao,
                cpf = funcionario.cpf.value,
            )

        every { repositoryMock.buscarPorCpf(request.cpf) } returns null
        every { mapperMock.toDomain(request) } returns funcionario
        every { repositoryMock.salvar(funcionario) } returns funcionario
        every { mapperMock.toResponse(funcionario) } returns response

        val result = useCase.executar(request)

        assertEquals(response, result)

        verify(exactly = 1) { repositoryMock.buscarPorCpf(request.cpf) }
        verify(exactly = 1) { mapperMock.toDomain(request) }
        verify(exactly = 1) { repositoryMock.salvar(funcionario) }
        verify(exactly = 1) { mapperMock.toResponse(funcionario) }
    }

    @Test
    @DisplayName("Dado CPF já registrado, quando criar funcionário, então deve lançar exceção")
    fun givenCpfAlreadyRegistered_whenCreatingFuncionario_thenThrowException() {
        val request =
            FuncionarioRequest(
                nome = "Maria Santos",
                cargo = "ATENDENTE",
                cpf = "98765432100",
            )

        val funcionarioExistente =
            Funcionario.criar(
                nome = request.nome,
                cargo = request.cargo,
                cpf = request.cpf,
            )

        every { repositoryMock.buscarPorCpf(request.cpf) } returns funcionarioExistente

        val exception =
            assertFailsWith<FuncionarioException> {
                useCase.executar(request)
            }

        assertEquals("Funcionário já cadastrado", exception.message)

        verify(exactly = 1) { repositoryMock.buscarPorCpf(request.cpf) }
        verify(exactly = 0) { mapperMock.toDomain(any()) }
        verify(exactly = 0) { repositoryMock.salvar(any()) }
    }

    @Test
    @DisplayName("Dado cargo inválido, quando criar funcionário, então deve lançar exceção")
    fun givenInvalidCargo_whenCreatingFuncionario_thenThrowException() {
        val request =
            FuncionarioRequest(
                nome = "Pedro Costa",
                cargo = "CARGO_INEXISTENTE",
                cpf = "11122233344",
            )

        every { repositoryMock.buscarPorCpf(request.cpf) } returns null
        every { mapperMock.toDomain(request) } throws IllegalArgumentException("Cargo inválido!")

        val exception =
            assertFailsWith<IllegalArgumentException> {
                useCase.executar(request)
            }

        assertEquals("Cargo inválido!", exception.message)

        verify(exactly = 1) { repositoryMock.buscarPorCpf(request.cpf) }
        verify(exactly = 1) { mapperMock.toDomain(request) }
        verify(exactly = 0) { repositoryMock.salvar(any()) }
    }

    @Test
    @DisplayName("Dado erro no repositório, quando criar funcionário, então deve propagar a exceção")
    fun givenRepositoryError_whenCreatingFuncionario_thenThrowException() {
        val request =
            FuncionarioRequest(
                nome = "Ana Oliveira",
                cargo = "MECANICO",
                cpf = "55566677788",
            )

        val funcionario =
            Funcionario.criar(
                nome = request.nome,
                cargo = request.cargo,
                cpf = request.cpf,
            )

        every { repositoryMock.buscarPorCpf(request.cpf) } returns null
        every { mapperMock.toDomain(request) } returns funcionario
        every { repositoryMock.salvar(funcionario) } throws RuntimeException("Erro ao salvar no banco")

        val exception =
            assertFailsWith<RuntimeException> {
                useCase.executar(request)
            }

        assertEquals("Erro ao salvar no banco", exception.message)

        verify(exactly = 1) { repositoryMock.buscarPorCpf(request.cpf) }
        verify(exactly = 1) { mapperMock.toDomain(request) }
        verify(exactly = 1) { repositoryMock.salvar(funcionario) }
        verify(exactly = 0) { mapperMock.toResponse(any()) }
    }
}
