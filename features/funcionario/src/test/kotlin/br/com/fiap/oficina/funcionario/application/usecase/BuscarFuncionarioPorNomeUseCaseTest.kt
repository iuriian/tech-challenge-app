package br.com.fiap.oficina.funcionario.application.usecase

import br.com.fiap.oficina.funcionario.application.dto.FuncionarioResponse
import br.com.fiap.oficina.funcionario.application.mapper.FuncionarioMapper
import br.com.fiap.oficina.funcionario.domain.Funcionario
import br.com.fiap.oficina.funcionario.domain.FuncionarioRepository
import br.com.fiap.oficina.shared.domain.exception.EntityNotFoundException
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

@DisplayName("Use case - Buscar funcionario por nome")
class BuscarFuncionarioPorNomeUseCaseTest {
    private val repositoryMock = mockk<FuncionarioRepository>(relaxed = true)
    private val mapperMock = mockk<FuncionarioMapper>(relaxed = true)
    private val useCase = BuscarFuncionarioPorNomeUseCaseImpl(repositoryMock, mapperMock)

    @Test
    @DisplayName("Dado nome correto, quando buscar funcionario por nome, então deve recuperar o funcionario")
    fun givenTheNameIsCorrect_whenSearchFuncionarioByName_thenRetrievesFuncionario() {
        val funcionario =
            Funcionario.reconstruir(
                id = "00000000-0000-0000-0000-000000000100",
                nome = "Test",
                cpf = "01234567890",
                cargo = "Atendente",
            )

        val response =
            FuncionarioResponse(
                id = funcionario.id.value.toString(),
                nome = funcionario.nome,
                cpf = funcionario.cpf.value,
                cargoDescricao = funcionario.cargo.descricao,
            )

        every { repositoryMock.buscarPorNome(any()) } returns funcionario
        every { mapperMock.toResponse(any()) } returns response

        val result = useCase.executar("Test")

        assertEquals(result.nome, "Test")

        verify(exactly = 1) { repositoryMock.buscarPorNome(any()) }
        verify(exactly = 1) { mapperMock.toResponse(any()) }
    }

    @Test
    @DisplayName("Dado nome inexistente, quando buscar funcionario por nome, então deve lançar exceção")
    fun givenTheNameDoesNotExist_whenSearchFuncionarioByName_thenThrowsException() {
        val nome = "Test"

        every { repositoryMock.buscarPorNome(any()) } returns null

        val exception =
            assertThrows<EntityNotFoundException> {
                useCase.executar(nome)
            }

        assertEquals("Funcionário com nome $nome não encontrado", exception.message)

        verify(exactly = 1) { repositoryMock.buscarPorNome(any()) }
        verify(exactly = 0) { mapperMock.toResponse(any()) }
    }
}
