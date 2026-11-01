package br.com.fiap.oficina.servico.application.usecase

import br.com.fiap.oficina.servico.ServicoFixtures
import br.com.fiap.oficina.servico.application.mapper.ServicoMapper
import br.com.fiap.oficina.servico.domain.Servico
import br.com.fiap.oficina.servico.domain.ServicoId
import br.com.fiap.oficina.servico.domain.ServicoRepository
import br.com.fiap.oficina.shared.domain.exception.EntityNotFoundException
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

@DisplayName("Use case - Atualizar Serviço")
class AtualizarServicoUseCaseTest {
    private val repositoryMock = mockk<ServicoRepository>()
    private val usecase = AtualizarServicoUseCaseImpl(repositoryMock, ServicoMapper())

    @Test
    @DisplayName("Dado um serviço existente, quando atualizar, então deve alterar descrição e valor preservando o id")
    fun givenExistingServico_whenUpdating_thenChangeDescriptionAndValueKeepingId() {
        val id = ServicoId.toUUID(ServicoFixtures.ID_1)
        val capturado = slot<Servico>()

        every { repositoryMock.buscarPorId(id) } returns ServicoFixtures.servico()
        every { repositoryMock.salvar(capture(capturado)) } answers { capturado.captured }

        val response =
            usecase.executar(
                ServicoFixtures.ID_1,
                ServicoFixtures.request(
                    descricao = ServicoFixtures.DESCRICAO_ALTERNATIVA,
                    valor = ServicoFixtures.VALOR_ALTERNATIVO,
                ),
            )

        assertEquals(ServicoFixtures.ID_1, response.id)
        assertEquals(ServicoFixtures.DESCRICAO_ALTERNATIVA, response.descricao)
        assertEquals(ServicoFixtures.VALOR_ALTERNATIVO, response.valor)
        assertEquals(ServicoFixtures.ID_1, capturado.captured.id.toString())

        verify(exactly = 1) { repositoryMock.salvar(any()) }
    }

    @Test
    @DisplayName("Dado um serviço inativo, quando atualizar, então deve preservar o estado de ativação")
    fun givenInactiveServico_whenUpdating_thenPreserveActivationState() {
        every { repositoryMock.buscarPorId(any()) } returns ServicoFixtures.servico(ativo = false)
        every { repositoryMock.salvar(any()) } answers { firstArg() }

        val response = usecase.executar(ServicoFixtures.ID_1, ServicoFixtures.request())

        assertFalse(response.ativo)
    }

    @Test
    @DisplayName("Dado um id inexistente, quando atualizar, então deve lançar exceção e não persistir")
    fun givenNonExistentId_whenUpdating_thenThrowExceptionAndDoNotPersist() {
        every { repositoryMock.buscarPorId(any()) } returns null

        val exception =
            assertFailsWith<EntityNotFoundException> {
                usecase.executar(ServicoFixtures.ID_1, ServicoFixtures.request())
            }

        assertEquals("Serviço não encontrado com o id: ${ServicoFixtures.ID_1}", exception.message)

        verify(exactly = 0) { repositoryMock.salvar(any()) }
    }

    @Test
    @DisplayName("Dado um request com descrição em branco, quando atualizar, então não deve persistir")
    fun givenRequestWithBlankDescription_whenUpdating_thenDoNotPersist() {
        every { repositoryMock.buscarPorId(any()) } returns ServicoFixtures.servico()

        assertFailsWith<IllegalArgumentException> {
            usecase.executar(ServicoFixtures.ID_1, ServicoFixtures.request(descricao = " "))
        }

        verify(exactly = 0) { repositoryMock.salvar(any()) }
    }
}
