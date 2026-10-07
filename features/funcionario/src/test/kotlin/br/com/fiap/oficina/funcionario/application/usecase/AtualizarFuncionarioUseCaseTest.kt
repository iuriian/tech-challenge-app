package br.com.fiap.oficina.funcionario.application.usecase

import br.com.fiap.oficina.funcionario.application.dto.FuncionarioRequest
import br.com.fiap.oficina.funcionario.application.mapper.FuncionarioMapper
import br.com.fiap.oficina.funcionario.domain.Cargo
import br.com.fiap.oficina.funcionario.domain.Funcionario
import br.com.fiap.oficina.funcionario.domain.FuncionarioRepository
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
                nome = "Test atualizado",
                cargo = "Mecânico",
                cpf = CPF_NOVO,
            )

        val funcionario = funcionarioExistente()

        val salvo = slot<Funcionario>()

        every { repositoryMock.buscarPorId(any()) } returns funcionario
        every { repositoryMock.buscarPorCpf(any()) } returns funcionario
        every { repositoryMock.salvar(capture(salvo)) } returns funcionario

        val resultado = usecase.executar(ID_EXISTENTE, request)

        assertNotNull(resultado)
        assertEquals(funcionario.id, salvo.captured.id)
        assertEquals("Test atualizado", salvo.captured.nome)
        assertEquals(CPF_NOVO, salvo.captured.cpf.value)
        assertEquals(Cargo.MECANICO, salvo.captured.cargo)

        verify(exactly = 1) { repositoryMock.buscarPorId(any()) }
        verify(exactly = 1) { repositoryMock.buscarPorCpf(any()) }
        verify(exactly = 1) { repositoryMock.salvar(any()) }
        verify(exactly = 1) { mapperMock.toResponse(any()) }
    }

    @Test
    @DisplayName("Dado cpf inalterado, quando atualizar funcionario, então não deve consultar o cpf")
    fun givenUnchangedCpf_whenUpdateFuncionario_thenNotSearchByCpf() {
        val request =
            FuncionarioRequest(
                nome = "Test atualizado",
                cargo = "Mecânico",
                cpf = CPF_EXISTENTE,
            )

        val funcionario = funcionarioExistente()

        val salvo = slot<Funcionario>()

        every { repositoryMock.buscarPorId(any()) } returns funcionario
        every { repositoryMock.salvar(capture(salvo)) } returns funcionario

        val resultado = usecase.executar(ID_EXISTENTE, request)

        assertNotNull(resultado)
        assertEquals("Test atualizado", salvo.captured.nome)
        assertEquals(CPF_EXISTENTE, salvo.captured.cpf.value)

        verify(exactly = 1) { repositoryMock.buscarPorId(any()) }
        verify(exactly = 0) { repositoryMock.buscarPorCpf(any()) }
        verify(exactly = 1) { repositoryMock.salvar(any()) }
    }

    @Test
    @DisplayName("Dado cpf alterado para um disponível, quando atualizar funcionario, então deve salvar o novo cpf")
    fun givenAvailableCpf_whenUpdateFuncionario_thenSaveNewCpf() {
        val request =
            FuncionarioRequest(
                nome = "Test",
                cargo = "Atendente",
                cpf = CPF_NOVO,
            )

        val funcionario = funcionarioExistente()

        val salvo = slot<Funcionario>()

        every { repositoryMock.buscarPorId(any()) } returns funcionario
        every { repositoryMock.buscarPorCpf(request.cpf) } returns null
        every { repositoryMock.salvar(capture(salvo)) } returns funcionario

        val resultado = usecase.executar(ID_EXISTENTE, request)

        assertNotNull(resultado)
        assertEquals(CPF_NOVO, salvo.captured.cpf.value)

        verify(exactly = 1) { repositoryMock.buscarPorId(any()) }
        verify(exactly = 1) { repositoryMock.buscarPorCpf(request.cpf) }
        verify(exactly = 1) { repositoryMock.salvar(any()) }
    }

    @Test
    @DisplayName("Dado dados sem id, quando atualizar funcionario, então deve lançar exceção")
    fun givenDataWithoutId_whenUpdateFuncionario_thenThrowException() {
        val request =
            FuncionarioRequest(
                nome = "Test",
                cargo = "Mecânico",
                cpf = CPF_NOVO,
            )

        val exception =
            assertFailsWith<IllegalArgumentException> {
                usecase.executar("", request)
            }

        assertEquals("Id é obrigatório!", exception.message)

        verify(exactly = 0) { repositoryMock.buscarPorId(any()) }
        verify(exactly = 0) { repositoryMock.salvar(any()) }
    }

    @Test
    @DisplayName("Dado id em formato inválido, quando atualizar funcionario, então deve lançar exceção")
    fun givenMalformedId_whenUpdateFuncionario_thenThrowException() {
        val request =
            FuncionarioRequest(
                nome = "Test",
                cargo = "Mecânico",
                cpf = CPF_NOVO,
            )

        assertFailsWith<IllegalArgumentException> {
            usecase.executar("nao-e-um-uuid", request)
        }

        verify(exactly = 0) { repositoryMock.buscarPorId(any()) }
        verify(exactly = 0) { repositoryMock.salvar(any()) }
    }

    @Test
    @DisplayName("Dado dados com id inexistente, quando atualizar funcionario, então deve lançar exceção")
    fun givenDataWithNonExistentId_whenUpdateFuncionario_thenThrowException() {
        val request =
            FuncionarioRequest(
                nome = "Test",
                cargo = "Mecânico",
                cpf = CPF_NOVO,
            )

        every { repositoryMock.buscarPorId(any()) } returns null

        val exception =
            assertFailsWith<EntityNotFoundException> {
                usecase.executar(ID_EXISTENTE, request)
            }

        assertEquals("Funcionário não encontrado!", exception.message)

        verify(exactly = 1) { repositoryMock.buscarPorId(any()) }
        verify(exactly = 0) { repositoryMock.buscarPorCpf(any()) }
        verify(exactly = 0) { repositoryMock.salvar(any()) }
    }

    @Test
    @DisplayName("Dado dados com cpf já existente, quando atualizar funcionario, então deve lançar exceção")
    fun givenDataWithExistingCpf_whenUpdateFuncionario_thenThrowException() {
        val request =
            FuncionarioRequest(
                nome = "Test atualizado",
                cargo = "Mecânico",
                cpf = CPF_NOVO,
            )

        val outroFuncionario =
            Funcionario.reconstruir(
                id = "00000000-0000-0000-0000-000000000200",
                nome = "Test Existente",
                cargo = "Mecânico",
                cpf = CPF_NOVO,
            )

        every { repositoryMock.buscarPorId(any()) } returns funcionarioExistente()
        every { repositoryMock.buscarPorCpf(any()) } returns outroFuncionario

        val exception =
            assertFailsWith<BusinessRuleException> {
                usecase.executar(ID_EXISTENTE, request)
            }

        assertEquals("CPF já cadastrado!", exception.message)

        verify(exactly = 1) { repositoryMock.buscarPorId(any()) }
        verify(exactly = 1) { repositoryMock.buscarPorCpf(any()) }
        verify(exactly = 0) { repositoryMock.salvar(any()) }
    }

    private fun funcionarioExistente() = Funcionario.reconstruir(
        id = ID_EXISTENTE,
        nome = "Test",
        cargo = "Atendente",
        cpf = CPF_EXISTENTE,
    )

    private companion object {
        const val ID_EXISTENTE = "00000000-0000-0000-0000-000000000100"
        const val CPF_EXISTENTE = "01234567890"
        const val CPF_NOVO = "01234567891"
    }
}
