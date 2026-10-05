package br.com.fiap.oficina.funcionario.application.usecase

import br.com.fiap.oficina.funcionario.application.dto.FuncionarioResponse
import br.com.fiap.oficina.funcionario.application.mapper.FuncionarioMapper
import br.com.fiap.oficina.funcionario.domain.Funcionario
import br.com.fiap.oficina.funcionario.domain.FuncionarioRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

@DisplayName("Use case - Buscar funcionário por ID")
class BuscarFuncionarioPorIdUseCaseTest {
    private val repositoryMock = mockk<FuncionarioRepository>(relaxed = true)
    private val mapperMock = mockk<FuncionarioMapper>(relaxed = true)
    private val usecase = BuscarFuncionarioPorIdUseCaseImpl(repositoryMock, mapperMock)

    @Test
    @DisplayName("Dado id válido, quando buscar funcionario por id, então retornar funcionario")
    fun givenValidId_whenSearchFuncionarioById_thenReturnFuncionario() {
        val id = "00000000-0000-0000-0000-000000000010"

        val funcionario =
            Funcionario.reconstruir(
                id = id,
                nome = "Nome",
                cpf = "01234567890",
                cargo = "ATENDENTE",
            )

        val response =
            FuncionarioResponse(
                id = funcionario.id.toString(),
                nome = funcionario.nome,
                cargoDescricao = funcionario.cargo.descricao,
                cpf = funcionario.cpf.value,
            )

        every { repositoryMock.buscarPorId(any()) } returns funcionario
        every { mapperMock.toResponse(any()) } returns response

        val resultado = usecase.executar(id)

        assertNotNull(resultado)
        assertEquals(resultado, response)

        verify(exactly = 1) { repositoryMock.buscarPorId(any()) }
        verify(exactly = 1) { mapperMock.toResponse(funcionario) }
    }

    @Test
    fun givenInvalidId_whenSearchFuncionarioById_thenThrowException() {
        val id = "00000000-0000-0000-0000-000000000010"

        every { repositoryMock.buscarPorId(any()) } returns null

        val exception =
            assertFailsWith<RuntimeException> {
                usecase.executar(id)
            }

        assertEquals("Funcionário não encontrado, id: $id", exception.message)

        verify(exactly = 1) { repositoryMock.buscarPorId(any()) }
    }
}
