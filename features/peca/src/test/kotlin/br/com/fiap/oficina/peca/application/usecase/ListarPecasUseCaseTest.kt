package br.com.fiap.oficina.peca.application.usecase

import br.com.fiap.oficina.peca.application.mapper.PecaMapper
import br.com.fiap.oficina.peca.application.usecase.PecaFixtures.ID_1
import br.com.fiap.oficina.peca.application.usecase.PecaFixtures.ID_2
import br.com.fiap.oficina.peca.application.usecase.PecaFixtures.peca
import br.com.fiap.oficina.peca.application.usecase.PecaFixtures.response
import br.com.fiap.oficina.peca.domain.PecaRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

@DisplayName("Use case - Listar Peças")
class ListarPecasUseCaseTest {
    private val repositoryMock = mockk<PecaRepository>()
    private val mapperMock = mockk<PecaMapper>()
    private val useCase = ListarPecasUseCaseImpl(repositoryMock, mapperMock)

    @Test
    @DisplayName("Dado peças cadastradas, quando listar, então deve retornar todas convertidas na ordem")
    fun givenRegisteredPecas_whenListing_thenReturnAllConvertedInOrder() {
        val peca1 = peca(id = ID_1, codigo = "PC-001")
        val peca2 = peca(id = ID_2, codigo = "PC-002")

        every { repositoryMock.listar() } returns listOf(peca1, peca2)
        every { mapperMock.toResponse(peca1) } returns response(peca1)
        every { mapperMock.toResponse(peca2) } returns response(peca2)

        val resultado = useCase.executar()

        assertEquals(listOf(response(peca1), response(peca2)), resultado)

        verify(exactly = 1) { repositoryMock.listar() }
        verify(exactly = 2) { mapperMock.toResponse(any()) }
    }

    @Test
    @DisplayName("Dado nenhuma peça cadastrada, quando listar, então deve retornar lista vazia")
    fun givenNoPecas_whenListing_thenReturnEmptyList() {
        every { repositoryMock.listar() } returns emptyList()

        assertTrue(useCase.executar().isEmpty())

        verify(exactly = 0) { mapperMock.toResponse(any()) }
    }

    @Test
    @DisplayName("Dado erro no repositório, quando listar, então deve propagar a exceção")
    fun givenRepositoryError_whenListing_thenPropagateException() {
        every { repositoryMock.listar() } throws RuntimeException("Falha de conexão")

        val exception = assertFailsWith<RuntimeException> { useCase.executar() }

        assertEquals("Falha de conexão", exception.message)
    }
}
