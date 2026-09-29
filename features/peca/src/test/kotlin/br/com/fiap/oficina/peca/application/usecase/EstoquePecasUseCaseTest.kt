package br.com.fiap.oficina.peca.application.usecase

import br.com.fiap.oficina.peca.application.dto.EstoquePecaRequest
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
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

@DisplayName("Use case - Estoque de Peças")
class EstoquePecasUseCaseTest {
    private val repositoryMock = mockk<PecaRepository>()
    private val mapperMock = mockk<PecaMapper>()
    private val useCase = EstoquePecasUseCaseImpl(repositoryMock, mapperMock)

    @Test
    @DisplayName("Dado uma quantidade positiva, quando movimentar estoque, então deve repor as peças")
    fun givenPositiveQuantity_whenMovingStock_thenReplenishPecas() {
        val salva = slot<Peca>()

        every { repositoryMock.buscarPorCodigo(CODIGO) } returns peca(quantidade = 10)
        every { repositoryMock.salvar(capture(salva)) } answers { salva.captured }
        every { mapperMock.toResponse(any()) } answers { response(firstArg()) }

        val resultado = useCase.executar(CODIGO, EstoquePecaRequest(quantidade = 5))

        assertEquals(15, resultado.qtdEstoque)
        assertEquals(15, salva.captured.quantidade)

        verify(exactly = 1) { repositoryMock.salvar(any()) }
    }

    @Test
    @DisplayName("Dado uma quantidade negativa, quando movimentar estoque, então deve retirar as peças")
    fun givenNegativeQuantity_whenMovingStock_thenWithdrawPecas() {
        val salva = slot<Peca>()

        every { repositoryMock.buscarPorCodigo(CODIGO) } returns peca(quantidade = 10)
        every { repositoryMock.salvar(capture(salva)) } answers { salva.captured }
        every { mapperMock.toResponse(any()) } answers { response(firstArg()) }

        val resultado = useCase.executar(CODIGO, EstoquePecaRequest(quantidade = -4))

        assertEquals(6, resultado.qtdEstoque)
        assertEquals(6, salva.captured.quantidade)
    }

    @Test
    @DisplayName("Dado uma retirada igual ao estoque, quando movimentar estoque, então deve zerar o estoque")
    fun givenWithdrawalEqualToStock_whenMovingStock_thenStockBecomesZero() {
        val salva = slot<Peca>()

        every { repositoryMock.buscarPorCodigo(CODIGO) } returns peca(quantidade = 10)
        every { repositoryMock.salvar(capture(salva)) } answers { salva.captured }
        every { mapperMock.toResponse(any()) } answers { response(firstArg()) }

        assertEquals(0, useCase.executar(CODIGO, EstoquePecaRequest(quantidade = -10)).qtdEstoque)
    }

    @Test
    @DisplayName("Dado uma retirada maior que o estoque, quando movimentar estoque, então deve lançar exceção")
    fun givenWithdrawalGreaterThanStock_whenMovingStock_thenThrowException() {
        every { repositoryMock.buscarPorCodigo(CODIGO) } returns peca(quantidade = 5)

        val exception =
            assertFailsWith<IllegalArgumentException> {
                useCase.executar(CODIGO, EstoquePecaRequest(quantidade = -6))
            }

        assertEquals("Quantidade em estoque insuficiente", exception.message)

        verify(exactly = 0) { repositoryMock.salvar(any()) }
    }

    @Test
    @DisplayName("Dado quantidade zero, quando movimentar estoque, então deve rejeitar antes de consultar a peça")
    fun givenZeroQuantity_whenMovingStock_thenRejectBeforeLookup() {
        val exception =
            assertFailsWith<IllegalArgumentException> {
                useCase.executar(CODIGO, EstoquePecaRequest(quantidade = 0))
            }

        assertEquals("Valor inválido para atualização de estoque", exception.message)

        verify(exactly = 0) { repositoryMock.buscarPorCodigo(any()) }
        verify(exactly = 0) { repositoryMock.salvar(any()) }
    }

    @Test
    @DisplayName("Dado um código inexistente, quando movimentar estoque, então deve lançar exceção")
    fun givenNonExistentCodigo_whenMovingStock_thenThrowException() {
        every { repositoryMock.buscarPorCodigo("PC-404") } returns null

        val exception =
            assertFailsWith<IllegalArgumentException> {
                useCase.executar("PC-404", EstoquePecaRequest(quantidade = 5))
            }

        assertEquals("Peça não encontrada para o estoque", exception.message)

        verify(exactly = 0) { repositoryMock.salvar(any()) }
    }

    @Test
    @DisplayName("Dado uma movimentação, quando executada, então não deve alterar os demais campos")
    fun givenStockMovement_whenExecuted_thenOtherFieldsRemainUnchanged() {
        val original = peca(quantidade = 10, ativo = true)
        val salva = slot<Peca>()

        every { repositoryMock.buscarPorCodigo(CODIGO) } returns original
        every { repositoryMock.salvar(capture(salva)) } answers { salva.captured }
        every { mapperMock.toResponse(any()) } answers { response(firstArg()) }

        useCase.executar(CODIGO, EstoquePecaRequest(quantidade = 3))

        assertEquals(13, salva.captured.quantidade)
        assertEquals(original.id, salva.captured.id)
        assertEquals(original.codigo, salva.captured.codigo)
        assertEquals(original.nome, salva.captured.nome)
        assertEquals(original.precoCompra, salva.captured.precoCompra)
        assertEquals(original.precoVenda, salva.captured.precoVenda)
        assertEquals(original.ativo, salva.captured.ativo)
    }
}
