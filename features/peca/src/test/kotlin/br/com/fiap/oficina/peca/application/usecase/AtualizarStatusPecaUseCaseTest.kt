package br.com.fiap.oficina.peca.application.usecase

import br.com.fiap.oficina.peca.application.dto.StatusPecaRequest
import br.com.fiap.oficina.peca.application.mapper.PecaMapper
import br.com.fiap.oficina.peca.application.usecase.PecaFixtures.CODIGO
import br.com.fiap.oficina.peca.application.usecase.PecaFixtures.peca
import br.com.fiap.oficina.peca.application.usecase.PecaFixtures.response
import br.com.fiap.oficina.peca.domain.Peca
import br.com.fiap.oficina.peca.domain.PecaRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

@DisplayName("Use case - Atualizar Status da Peça")
class AtualizarStatusPecaUseCaseTest {
    private val repositoryMock = mockk<PecaRepository>()
    private val mapperMock = mockk<PecaMapper>()
    private val useCase = AtualizarStatusPecaUseCaseImpl(repositoryMock, mapperMock)

    @Test
    @DisplayName("Dado uma peça inativa, quando ativar, então deve persistir como ativa")
    fun givenInactivePeca_whenActivating_thenPersistAsActive() {
        val salva = slot<Peca>()

        every { repositoryMock.buscarPorCodigo(CODIGO) } returns peca(ativo = false)
        every { repositoryMock.salvar(capture(salva)) } answers { salva.captured }
        every { mapperMock.toResponse(any()) } answers { response(firstArg()) }

        val resultado = useCase.executar(CODIGO, StatusPecaRequest(ativo = true))

        assertTrue(resultado.ativo)
        assertTrue(salva.captured.ativo)

        verify(exactly = 1) { repositoryMock.salvar(any()) }
    }

    @Test
    @DisplayName("Dado uma peça ativa, quando desativar, então deve persistir como inativa")
    fun givenActivePeca_whenDeactivating_thenPersistAsInactive() {
        val salva = slot<Peca>()

        every { repositoryMock.buscarPorCodigo(CODIGO) } returns peca(ativo = true)
        every { repositoryMock.salvar(capture(salva)) } answers { salva.captured }
        every { mapperMock.toResponse(any()) } answers { response(firstArg()) }

        val resultado = useCase.executar(CODIGO, StatusPecaRequest(ativo = false))

        assertFalse(resultado.ativo)
        assertFalse(salva.captured.ativo)
    }

    @Test
    @DisplayName("Dado um request sem status, quando atualizar, então deve desativar por padrão")
    fun givenRequestWithoutStatus_whenUpdating_thenDeactivateByDefault() {
        val salva = slot<Peca>()

        every { repositoryMock.buscarPorCodigo(CODIGO) } returns peca(ativo = true)
        every { repositoryMock.salvar(capture(salva)) } answers { salva.captured }
        every { mapperMock.toResponse(any()) } answers { response(firstArg()) }

        assertFalse(useCase.executar(CODIGO, StatusPecaRequest()).ativo)
    }

    @Test
    @DisplayName("Dado um código inexistente, quando atualizar status, então deve lançar exceção")
    fun givenNonExistentCodigo_whenUpdatingStatus_thenThrowException() {
        every { repositoryMock.buscarPorCodigo("PC-404") } returns null

        val exception =
            assertFailsWith<IllegalArgumentException> {
                useCase.executar("PC-404", StatusPecaRequest(ativo = true))
            }

        assertEquals("Peça não encontrada para PC-404", exception.message)

        verify(exactly = 0) { repositoryMock.salvar(any()) }
    }

    @Test
    @DisplayName("Dado erro ao salvar, quando atualizar status, então deve propagar a exceção")
    fun givenSaveError_whenUpdatingStatus_thenPropagateException() {
        every { repositoryMock.buscarPorCodigo(CODIGO) } returns peca(ativo = false)
        every { repositoryMock.salvar(any()) } throws RuntimeException("Erro ao salvar no banco")

        val exception =
            assertFailsWith<RuntimeException> {
                useCase.executar(CODIGO, StatusPecaRequest(ativo = true))
            }

        assertEquals("Erro ao salvar no banco", exception.message)

        verify(exactly = 0) { mapperMock.toResponse(any()) }
    }
}
