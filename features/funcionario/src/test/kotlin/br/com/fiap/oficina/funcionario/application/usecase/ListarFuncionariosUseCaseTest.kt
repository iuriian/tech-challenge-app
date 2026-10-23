package br.com.fiap.oficina.funcionario.application.usecase

import br.com.fiap.oficina.funcionario.application.mapper.FuncionarioMapper
import br.com.fiap.oficina.funcionario.domain.Funcionario
import br.com.fiap.oficina.funcionario.domain.FuncionarioRepository
import br.com.fiap.oficina.shared.domain.valueobject.CPF
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Use case - Listar Funcionários")
class ListarFuncionariosUseCaseTest {
    private val repositoryMock = mockk<FuncionarioRepository>()
    private val mapperMock: FuncionarioMapper = mockk(relaxed = true)
    private val usecase = ListarFuncionariosUseCaseImpl(repositoryMock, mapperMock)

    @Test
    @DisplayName("Dado funcionários cadastrados, quando listar funcionários, então deve retornar todos os funcionários")
    fun givenRegisteredFuncionarios_whenListingFuncionarios_thenReturnAllFuncionarios() {
        val employees =
            CPFS_VALIDOS.mapIndexed { indice, cpf ->
                Funcionario.criar("Nome $indice", CPF(cpf), "Atendente")
            }

        every { repositoryMock.listarTodos() } returns employees

        val result = usecase.executar()

        assertEquals(employees.size, result.size)

        verify(exactly = 1) { repositoryMock.listarTodos() }
        verify(exactly = 3) { mapperMock.toResponse(any()) }
    }

    @Test
    @DisplayName("Dado nenhum funcionário cadastrado, quando listar funcionários, então deve retornar lista vazia")
    fun givenNoFuncionarios_whenListingFuncionarios_thenReturnEmptyList() {
        every { repositoryMock.listarTodos() } returns emptyList()

        val result = usecase.executar()

        assertEquals(0, result.size)

        verify(exactly = 1) { repositoryMock.listarTodos() }
        verify(exactly = 0) { mapperMock.toResponse(any()) }
    }

    private companion object {
        val CPFS_VALIDOS = listOf("01234567890", "98765432100", "12345678909")
    }
}
