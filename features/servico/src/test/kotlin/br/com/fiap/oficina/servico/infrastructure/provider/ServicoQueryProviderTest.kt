package br.com.fiap.oficina.servico.infrastructure.provider

import br.com.fiap.oficina.servico.ServicoFixtures
import br.com.fiap.oficina.servico.domain.ServicoId
import br.com.fiap.oficina.servico.domain.ServicoRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.UUID

@DisplayName("Infraestrutura - Provider de consulta de Serviço")
class ServicoQueryProviderTest {
    private val repositoryMock = mockk<ServicoRepository>()
    private val mapperMock = mockk<ServicoQueryMapper>()
    private val provider = ServicoQueryProvider(repositoryMock, mapperMock)

    @Test
    @DisplayName("Dado um id existente, quando buscar por id, então deve retornar a resposta convertida")
    fun givenExistingId_whenSearchingById_thenReturnConvertedResponse() {
        val id = UUID.fromString(ServicoFixtures.ID_1)
        val servico = ServicoFixtures.servico()
        val dto = ServicoFixtures.queryDto(servico)

        every { repositoryMock.buscarPorId(ServicoId(id)) } returns servico
        every { mapperMock.toProviderResponse(servico) } returns dto

        assertSame(dto, provider.buscarPorId(id))

        verify(exactly = 1) { repositoryMock.buscarPorId(ServicoId(id)) }
        verify(exactly = 1) { mapperMock.toProviderResponse(servico) }
    }

    @Test
    @DisplayName("Dado um id inexistente, quando buscar por id, então deve retornar nulo sem converter")
    fun givenNonExistentId_whenSearchingById_thenReturnNullWithoutConverting() {
        val id = UUID.randomUUID()

        every { repositoryMock.buscarPorId(ServicoId(id)) } returns null

        assertNull(provider.buscarPorId(id))

        verify(exactly = 1) { repositoryMock.buscarPorId(ServicoId(id)) }
        verify(exactly = 0) { mapperMock.toProviderResponse(any()) }
    }

    @Test
    @DisplayName("Dado um uuid da porta, quando buscar por id, então deve envolvê-lo em ServicoId")
    fun givenPortUuid_whenSearchingById_thenWrapItInServicoId() {
        val id = UUID.fromString(ServicoFixtures.ID_2)
        val capturado = slot<ServicoId>()

        every { repositoryMock.buscarPorId(capture(capturado)) } returns null

        provider.buscarPorId(id)

        assertEquals(id, capturado.captured.value)
    }

    @Test
    @DisplayName("Dado um serviço inativo, quando buscar por id, então deve retorná-lo sem filtrar pela situação")
    fun givenInactiveServico_whenSearchingById_thenReturnItWithoutFilteringByStatus() {
        val id = UUID.fromString(ServicoFixtures.ID_1)
        val servico = ServicoFixtures.servico(ativo = false)
        val dto = ServicoFixtures.queryDto(servico)

        every { repositoryMock.buscarPorId(ServicoId(id)) } returns servico
        every { mapperMock.toProviderResponse(servico) } returns dto

        assertSame(dto, provider.buscarPorId(id))
    }
}
