package br.com.fiap.oficina.peca.application.usecase

import br.com.fiap.oficina.peca.application.mapper.PecaMapper
import br.com.fiap.oficina.peca.application.usecase.PecaFixtures.CODIGO
import br.com.fiap.oficina.peca.application.usecase.PecaFixtures.peca
import br.com.fiap.oficina.peca.application.usecase.PecaFixtures.response
import br.com.fiap.oficina.peca.domain.PecaRepository
import br.com.fiap.oficina.shared.domain.exception.EntityNotFoundException
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

@DisplayName("Use case - Buscar Peça por Código")
class BuscarPecaPorCodigoUseCaseTest {
    private val repositoryMock = mockk<PecaRepository>()
    private val mapperMock = mockk<PecaMapper>()
    private val useCase = BuscarPecaPorCodigoUseCaseImpl(repositoryMock, mapperMock)

    @Test
    @DisplayName("Dado um código existente, quando buscar, então deve retornar a peça")
    fun givenExistingCodigo_whenSearching_thenReturnPeca() {
        val peca = peca()
        val response = response(peca)

        every { repositoryMock.buscarPorCodigo(CODIGO) } returns peca
        every { mapperMock.toResponse(peca) } returns response

        assertEquals(response, useCase.executar(CODIGO))

        verify(exactly = 1) { repositoryMock.buscarPorCodigo(CODIGO) }
        verify(exactly = 1) { mapperMock.toResponse(peca) }
    }

    @Test
    @DisplayName("Dado um código inexistente, quando buscar, então deve lançar exceção")
    fun givenNonExistentCodigo_whenSearching_thenThrowException() {
        every { repositoryMock.buscarPorCodigo("PC-404") } returns null

        val exception = assertFailsWith<EntityNotFoundException> { useCase.executar("PC-404") }

        assertEquals("Peça não encontrada! \nCódigo desconhecido.", exception.message)

        verify(exactly = 0) { mapperMock.toResponse(any()) }
    }

    @Test
    @DisplayName("Dado erro no repositório, quando buscar, então deve propagar a exceção")
    fun givenRepositoryError_whenSearching_thenPropagateException() {
        every { repositoryMock.buscarPorCodigo(CODIGO) } throws RuntimeException("Falha de conexão")

        val exception = assertFailsWith<RuntimeException> { useCase.executar(CODIGO) }

        assertEquals("Falha de conexão", exception.message)

        verify(exactly = 0) { mapperMock.toResponse(any()) }
    }
}
