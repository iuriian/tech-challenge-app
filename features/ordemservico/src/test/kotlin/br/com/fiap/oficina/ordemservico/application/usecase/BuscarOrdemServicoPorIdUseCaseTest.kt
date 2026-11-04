package br.com.fiap.oficina.ordemservico.application.usecase

import br.com.fiap.oficina.ordemservico.OrdemServicoFixtures
import br.com.fiap.oficina.ordemservico.application.mapper.ItemOrcamentoMapper
import br.com.fiap.oficina.ordemservico.application.mapper.OrdemServicoMapper
import br.com.fiap.oficina.ordemservico.domain.OrdemServicoId
import br.com.fiap.oficina.ordemservico.domain.OrdemServicoRepository
import br.com.fiap.oficina.shared.domain.exception.EntityNotFoundException
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

@DisplayName("Use case - Buscar Ordem de Serviço por id")
class BuscarOrdemServicoPorIdUseCaseTest {
    private val repositoryMock = mockk<OrdemServicoRepository>()
    private val usecase = BuscarOrdemServicoPorIdUseCaseImpl(repositoryMock, OrdemServicoMapper(ItemOrcamentoMapper()))

    @Test
    @DisplayName("Dado um id existente, quando buscar, então deve retornar o response")
    fun givenExistingId_whenSearching_thenReturnResponse() {
        val id = OrdemServicoId.toUUID(OrdemServicoFixtures.ID)

        every { repositoryMock.buscarPorId(id) } returns OrdemServicoFixtures.ordemServico()

        val response = usecase.executar(OrdemServicoFixtures.ID)

        assertEquals(OrdemServicoFixtures.ID, response.id)
        assertEquals(OrdemServicoFixtures.DESCRICAO, response.descricao)
        assertEquals(2, response.itens.size)

        verify(exactly = 1) { repositoryMock.buscarPorId(id) }
    }

    @Test
    @DisplayName("Dado um id inexistente, quando buscar, então deve lançar exceção")
    fun givenNonExistentId_whenSearching_thenThrowException() {
        every { repositoryMock.buscarPorId(any()) } returns null

        val exception = assertFailsWith<EntityNotFoundException> { usecase.executar(OrdemServicoFixtures.ID) }

        assertEquals("Ordem de serviço não encontrada, id: ${OrdemServicoFixtures.ID}", exception.message)
    }

    @Test
    @DisplayName("Dado um id inválido, quando buscar, então deve lançar exceção sem consultar o repositório")
    fun givenInvalidId_whenSearching_thenThrowExceptionWithoutQueryingRepository() {
        assertFailsWith<IllegalArgumentException> { usecase.executar(OrdemServicoFixtures.ID_INVALIDO) }

        verify(exactly = 0) { repositoryMock.buscarPorId(any()) }
    }
}
