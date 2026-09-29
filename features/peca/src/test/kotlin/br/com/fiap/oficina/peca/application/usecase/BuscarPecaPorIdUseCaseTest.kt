package br.com.fiap.oficina.peca.application.usecase

import br.com.fiap.oficina.peca.application.mapper.PecaMapper
import br.com.fiap.oficina.peca.application.usecase.PecaFixtures.ID_1
import br.com.fiap.oficina.peca.application.usecase.PecaFixtures.peca
import br.com.fiap.oficina.peca.application.usecase.PecaFixtures.response
import br.com.fiap.oficina.peca.domain.PecaId
import br.com.fiap.oficina.peca.domain.PecaRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

@DisplayName("Use case - Buscar Peça por Id")
class BuscarPecaPorIdUseCaseTest {
    private val repositoryMock = mockk<PecaRepository>()
    private val mapperMock = mockk<PecaMapper>()
    private val useCase = BuscarPecaPorIdUseCaseImpl(repositoryMock, mapperMock)

    @Test
    @DisplayName("Dado um id existente, quando buscar, então deve retornar a peça")
    fun givenExistingId_whenSearching_thenReturnPeca() {
        val peca = peca()
        val response = response(peca)

        every { repositoryMock.buscarPorId(PecaId.toUUID(ID_1)) } returns peca
        every { mapperMock.toResponse(peca) } returns response

        assertEquals(response, useCase.executar(ID_1))

        verify(exactly = 1) { repositoryMock.buscarPorId(PecaId.toUUID(ID_1)) }
        verify(exactly = 1) { mapperMock.toResponse(peca) }
    }

    @Test
    @DisplayName("Dado um id inexistente, quando buscar, então deve lançar exceção")
    fun givenNonExistentId_whenSearching_thenThrowException() {
        every { repositoryMock.buscarPorId(PecaId.toUUID(ID_1)) } returns null

        val exception = assertFailsWith<IllegalArgumentException> { useCase.executar(ID_1) }

        assertEquals("Peça não encontrada, id: $ID_1", exception.message)

        verify(exactly = 0) { mapperMock.toResponse(any()) }
    }

    @Test
    @DisplayName("Dado um id fora do formato UUID, quando buscar, então deve lançar exceção")
    fun givenMalformedUuid_whenSearching_thenThrowException() {
        assertFailsWith<IllegalArgumentException> { useCase.executar("nao-e-um-uuid") }

        verify(exactly = 0) { repositoryMock.buscarPorId(any()) }
    }
}
