package br.com.fiap.oficina.peca.application.usecase

import br.com.fiap.oficina.peca.application.mapper.PecaMapper
import br.com.fiap.oficina.peca.application.usecase.PecaFixtures.peca
import br.com.fiap.oficina.peca.application.usecase.PecaFixtures.request
import br.com.fiap.oficina.peca.application.usecase.PecaFixtures.response
import br.com.fiap.oficina.peca.domain.PecaRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

@DisplayName("Use case - Cadastrar Peça")
class CadastrarPecaUseCaseTest {
    private val repositoryMock = mockk<PecaRepository>()
    private val mapperMock = mockk<PecaMapper>()
    private val useCase = CadastrarPecaUseCaseImpl(repositoryMock, mapperMock)

    @Test
    @DisplayName("Dado dados válidos, quando cadastrar peça, então deve ter sucesso")
    fun givenValidData_whenRegisteringPeca_thenShouldSucceed() {
        val request = request()
        val peca = peca()
        val response = response(peca)

        every { repositoryMock.buscarPorCodigo(request.codigo) } returns null
        every { mapperMock.toDomain(request) } returns peca
        every { repositoryMock.salvar(peca) } returns peca
        every { mapperMock.toResponse(peca) } returns response

        assertEquals(response, useCase.executar(request))

        verify(exactly = 1) { repositoryMock.buscarPorCodigo(request.codigo) }
        verify(exactly = 1) { mapperMock.toDomain(request) }
        verify(exactly = 1) { repositoryMock.salvar(peca) }
        verify(exactly = 1) { mapperMock.toResponse(peca) }
    }

    @Test
    @DisplayName("Dado um código já cadastrado, quando cadastrar peça, então deve lançar exceção")
    fun givenCodigoAlreadyRegistered_whenRegisteringPeca_thenThrowException() {
        val request = request()

        every { repositoryMock.buscarPorCodigo(request.codigo) } returns peca()

        val exception = assertFailsWith<IllegalArgumentException> { useCase.executar(request) }

        assertEquals("Peça já cadastrada", exception.message)

        verify(exactly = 1) { repositoryMock.buscarPorCodigo(request.codigo) }
        verify(exactly = 0) { mapperMock.toDomain(any()) }
        verify(exactly = 0) { repositoryMock.salvar(any()) }
    }

    @Test
    @DisplayName("Dado dados inválidos, quando cadastrar peça, então deve propagar a exceção do mapper")
    fun givenInvalidData_whenRegisteringPeca_thenPropagateMapperException() {
        val request = request(nome = "   ")

        every { repositoryMock.buscarPorCodigo(request.codigo) } returns null
        every { mapperMock.toDomain(request) } throws IllegalArgumentException("Nome da peça é obrigatório")

        val exception = assertFailsWith<IllegalArgumentException> { useCase.executar(request) }

        assertEquals("Nome da peça é obrigatório", exception.message)

        verify(exactly = 1) { mapperMock.toDomain(request) }
        verify(exactly = 0) { repositoryMock.salvar(any()) }
    }

    @Test
    @DisplayName("Dado erro no repositório, quando cadastrar peça, então deve propagar a exceção")
    fun givenRepositoryError_whenRegisteringPeca_thenPropagateException() {
        val request = request()
        val peca = peca()

        every { repositoryMock.buscarPorCodigo(request.codigo) } returns null
        every { mapperMock.toDomain(request) } returns peca
        every { repositoryMock.salvar(peca) } throws RuntimeException("Erro ao salvar no banco")

        val exception = assertFailsWith<RuntimeException> { useCase.executar(request) }

        assertEquals("Erro ao salvar no banco", exception.message)

        verify(exactly = 1) { repositoryMock.salvar(peca) }
        verify(exactly = 0) { mapperMock.toResponse(any()) }
    }
}
