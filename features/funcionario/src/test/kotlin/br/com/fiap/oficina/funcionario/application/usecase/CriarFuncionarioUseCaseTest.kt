package br.com.fiap.oficina.funcionario.application.usecase

import br.com.fiap.oficina.funcionario.application.dto.FuncionarioRequest
import br.com.fiap.oficina.funcionario.application.dto.FuncionarioResponse
import br.com.fiap.oficina.funcionario.application.mapper.FuncionarioMapper
import br.com.fiap.oficina.funcionario.domain.Funcionario
import br.com.fiap.oficina.funcionario.domain.FuncionarioRepository
import br.com.fiap.oficina.shared.domain.exception.BusinessRuleException
import br.com.fiap.oficina.shared.domain.valueobject.CPF
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
                cargo = "Mecânico",
                cpf = CPF_VALIDO,
            )

        val funcionario = funcionarioDe(request)

        val response =
            FuncionarioResponse(
                id = funcionario.id.value.toString(),
                nome = funcionario.nome,
                cargoDescricao = funcionario.cargo.descricao,
                cpf = funcionario.cpf.valor,
            )

        every { mapperMock.toDomain(request) } returns funcionario
        every { repositoryMock.buscarPorCpf(funcionario.cpf) } returns null
        every { repositoryMock.salvar(funcionario) } returns funcionario
        every { mapperMock.toResponse(funcionario) } returns response

        val result = useCase.executar(request)

        assertEquals(response, result)

        verify(exactly = 1) { mapperMock.toDomain(request) }
        verify(exactly = 1) { repositoryMock.buscarPorCpf(funcionario.cpf) }
        verify(exactly = 1) { repositoryMock.salvar(funcionario) }
        verify(exactly = 1) { mapperMock.toResponse(funcionario) }
    }

    @Test
    @DisplayName("Dado CPF já registrado, quando criar funcionário, então deve lançar exceção")
    fun givenCpfAlreadyRegistered_whenCreatingFuncionario_thenThrowException() {
        val request =
            FuncionarioRequest(
                nome = "Maria Santos",
                cargo = "Atendente",
                cpf = CPF_OUTRO,
            )

        val funcionario = funcionarioDe(request)

        every { mapperMock.toDomain(request) } returns funcionario
        every { repositoryMock.buscarPorCpf(funcionario.cpf) } returns funcionarioDe(request)

        val exception =
            assertFailsWith<BusinessRuleException> {
                useCase.executar(request)
            }

        assertEquals("Funcionário já cadastrado", exception.message)

        verify(exactly = 1) { repositoryMock.buscarPorCpf(funcionario.cpf) }
        verify(exactly = 0) { repositoryMock.salvar(any()) }
        verify(exactly = 0) { mapperMock.toResponse(any()) }
    }

    @Test
    @DisplayName("Dado CPF com máscara já registrado, quando criar funcionário, então deve lançar exceção")
    fun givenMaskedCpfAlreadyRegistered_whenCreatingFuncionario_thenThrowException() {
        val request =
            FuncionarioRequest(
                nome = "Maria Santos",
                cargo = "Atendente",
                cpf = CPF_VALIDO_MASCARADO,
            )

        val funcionario = funcionarioDe(request)

        every { mapperMock.toDomain(request) } returns funcionario
        every { repositoryMock.buscarPorCpf(CPF(CPF_VALIDO)) } returns funcionario

        assertFailsWith<BusinessRuleException> {
            useCase.executar(request)
        }

        verify(exactly = 1) { repositoryMock.buscarPorCpf(CPF(CPF_VALIDO)) }
        verify(exactly = 0) { repositoryMock.salvar(any()) }
    }

    @Test
    @DisplayName("Dado cargo inválido, quando criar funcionário, então deve lançar exceção")
    fun givenInvalidCargo_whenCreatingFuncionario_thenThrowException() {
        val request =
            FuncionarioRequest(
                nome = "Pedro Costa",
                cargo = "CARGO_INEXISTENTE",
                cpf = CPF_TERCEIRO,
            )

        every { mapperMock.toDomain(request) } throws IllegalArgumentException("Cargo inválido!")

        val exception =
            assertFailsWith<IllegalArgumentException> {
                useCase.executar(request)
            }

        assertEquals("Cargo inválido!", exception.message)

        verify(exactly = 1) { mapperMock.toDomain(request) }
        verify(exactly = 0) { repositoryMock.buscarPorCpf(any()) }
        verify(exactly = 0) { repositoryMock.salvar(any()) }
    }

    @Test
    @DisplayName("Dado erro no repositório, quando criar funcionário, então deve propagar a exceção")
    fun givenRepositoryError_whenCreatingFuncionario_thenThrowException() {
        val request =
            FuncionarioRequest(
                nome = "Ana Oliveira",
                cargo = "Mecânico",
                cpf = CPF_QUARTO,
            )

        val funcionario = funcionarioDe(request)

        every { mapperMock.toDomain(request) } returns funcionario
        every { repositoryMock.buscarPorCpf(funcionario.cpf) } returns null
        every { repositoryMock.salvar(funcionario) } throws RuntimeException("Erro ao salvar no banco")

        val exception =
            assertFailsWith<RuntimeException> {
                useCase.executar(request)
            }

        assertEquals("Erro ao salvar no banco", exception.message)

        verify(exactly = 1) { repositoryMock.buscarPorCpf(funcionario.cpf) }
        verify(exactly = 1) { repositoryMock.salvar(funcionario) }
        verify(exactly = 0) { mapperMock.toResponse(any()) }
    }

    private fun funcionarioDe(request: FuncionarioRequest) = Funcionario.criar(
        nome = request.nome,
        cpf = CPF.from(request.cpf),
        cargo = request.cargo,
    )

    private companion object {
        const val CPF_VALIDO = "01234567890"
        const val CPF_VALIDO_MASCARADO = "012.345.678-90"
        const val CPF_OUTRO = "98765432100"
        const val CPF_TERCEIRO = "12345678909"
        const val CPF_QUARTO = "11122233396"
    }
}
