package br.com.fiap.oficina.peca.application.usecase

import br.com.fiap.oficina.peca.application.mapper.PecaMapper
import br.com.fiap.oficina.peca.application.usecase.PecaFixtures.CODIGO
import br.com.fiap.oficina.peca.application.usecase.PecaFixtures.ID_1
import br.com.fiap.oficina.peca.application.usecase.PecaFixtures.ID_2
import br.com.fiap.oficina.peca.application.usecase.PecaFixtures.peca
import br.com.fiap.oficina.peca.application.usecase.PecaFixtures.request
import br.com.fiap.oficina.peca.application.usecase.PecaFixtures.response
import br.com.fiap.oficina.peca.domain.Peca
import br.com.fiap.oficina.peca.domain.PecaId
import br.com.fiap.oficina.peca.domain.PecaRepository
import br.com.fiap.oficina.shared.domain.exception.BusinessRuleException
import br.com.fiap.oficina.shared.domain.exception.EntityNotFoundException
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

@DisplayName("Use case - Atualizar Peça")
class AtualizarPecaUseCaseTest {
    private val repositoryMock = mockk<PecaRepository>()
    private val mapperMock = mockk<PecaMapper>()
    private val useCase = AtualizarPecaUseCaseImpl(repositoryMock, mapperMock)

    @Test
    @DisplayName("Dado o mesmo código, quando atualizar peça, então não deve consultar por código")
    fun givenSameCodigo_whenUpdatingPeca_thenSkipCodigoLookup() {
        val existente = peca()
        val request = request(nome = "Pastilha cerâmica")
        val salva = slot<Peca>()

        every { repositoryMock.buscarPorId(PecaId.toUUID(ID_1)) } returns existente
        every { repositoryMock.salvar(capture(salva)) } answers { salva.captured }
        every { mapperMock.toResponse(any()) } answers { response(firstArg()) }

        val resultado = useCase.executar(ID_1, request)

        assertEquals("Pastilha cerâmica", resultado.nome)
        assertEquals(ID_1, resultado.id)
        assertEquals(CODIGO, resultado.codigo)

        verify(exactly = 0) { repositoryMock.buscarPorCodigo(any()) }
        verify(exactly = 1) { repositoryMock.salvar(any()) }
    }

    @Test
    @DisplayName("Dado um novo código livre, quando atualizar peça, então deve ter sucesso")
    fun givenNewAvailableCodigo_whenUpdatingPeca_thenShouldSucceed() {
        val existente = peca()
        val request = request(codigo = "PC-999")
        val salva = slot<Peca>()

        every { repositoryMock.buscarPorId(PecaId.toUUID(ID_1)) } returns existente
        every { repositoryMock.buscarPorCodigo("PC-999") } returns null
        every { repositoryMock.salvar(capture(salva)) } answers { salva.captured }
        every { mapperMock.toResponse(any()) } answers { response(firstArg()) }

        val resultado = useCase.executar(ID_1, request)

        // A validação de unicidade só se justifica se o código for de fato aplicado à peça.
        assertEquals("PC-999", salva.captured.codigo)
        assertEquals("PC-999", resultado.codigo)

        verify(exactly = 1) { repositoryMock.buscarPorCodigo("PC-999") }
        verify(exactly = 1) { repositoryMock.salvar(any()) }
    }

    @Test
    @DisplayName("Dado um novo código de outra peça, quando atualizar peça, então deve lançar exceção")
    fun givenNewCodigoOwnedByAnotherPeca_whenUpdatingPeca_thenThrowException() {
        val existente = peca()
        val outra = peca(id = ID_2, codigo = "PC-999")

        every { repositoryMock.buscarPorId(PecaId.toUUID(ID_1)) } returns existente
        every { repositoryMock.buscarPorCodigo("PC-999") } returns outra

        val exception =
            assertFailsWith<BusinessRuleException> {
                useCase.executar(ID_1, request(codigo = "PC-999"))
            }

        assertEquals("Código já cadastrado!", exception.message)

        verify(exactly = 0) { repositoryMock.salvar(any()) }
    }

    @Test
    @DisplayName("Dado um id em branco, quando atualizar peça, então deve lançar exceção")
    fun givenBlankId_whenUpdatingPeca_thenThrowException() {
        val exception =
            assertFailsWith<IllegalArgumentException> {
                useCase.executar("   ", request())
            }

        assertEquals("Id é obrigatório!", exception.message)

        verify(exactly = 0) { repositoryMock.buscarPorId(any()) }
    }

    @Test
    @DisplayName("Dado um id inexistente, quando atualizar peça, então deve lançar exceção")
    fun givenNonExistentId_whenUpdatingPeca_thenThrowException() {
        every { repositoryMock.buscarPorId(PecaId.toUUID(ID_1)) } returns null

        val exception =
            assertFailsWith<EntityNotFoundException> {
                useCase.executar(ID_1, request())
            }

        assertEquals("Peça não encontrada!", exception.message)

        verify(exactly = 0) { repositoryMock.salvar(any()) }
    }

    @Test
    @DisplayName("Dado uma peça existente, quando atualizar, então deve preservar quantidade e status")
    fun givenExistingPeca_whenUpdating_thenPreserveQuantityAndStatus() {
        val existente = peca(quantidade = 42, ativo = true)
        val salva = slot<Peca>()

        every { repositoryMock.buscarPorId(PecaId.toUUID(ID_1)) } returns existente
        every { repositoryMock.salvar(capture(salva)) } answers { salva.captured }
        every { mapperMock.toResponse(any()) } answers { response(firstArg()) }

        val resultado = useCase.executar(ID_1, request(quantidade = 7))

        assertEquals(42, resultado.qtdEstoque)
        assertEquals(true, resultado.ativo)
    }
}
