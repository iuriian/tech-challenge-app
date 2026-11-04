package br.com.fiap.oficina.ordemservico.infrastructure.persistence

import br.com.fiap.oficina.ordemservico.OrdemServicoFixtures
import br.com.fiap.oficina.ordemservico.domain.ClienteRef
import br.com.fiap.oficina.ordemservico.domain.OrdemServicoId
import br.com.fiap.oficina.ordemservico.domain.OrdemServicoStatus
import br.com.fiap.oficina.ordemservico.domain.VeiculoRef
import br.com.fiap.oficina.ordemservico.infrastructure.mapper.ItemOrcamentoJPAMapper
import br.com.fiap.oficina.ordemservico.infrastructure.mapper.OrdemServicoJPAMapper
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.Optional
import java.util.UUID

@DisplayName("Infraestrutura - Repositório de Ordem de Serviço")
class OrdemServicoRepositoryImplTest {
    private val jpaRepositoryMock = mockk<OrdemServicoJPARepository>()
    private val repository =
        OrdemServicoRepositoryImpl(jpaRepositoryMock, OrdemServicoJPAMapper(ItemOrcamentoJPAMapper()))

    private val id = OrdemServicoId.toUUID(OrdemServicoFixtures.ID)

    @Test
    @DisplayName("Dado uma ordem de serviço, quando salvar, então deve persistir e devolver o domínio convertido")
    fun givenOrdemServico_whenSaving_thenPersistAndReturnConvertedDomain() {
        every { jpaRepositoryMock.save(any()) } answers { firstArg() }

        val salva = repository.salvar(OrdemServicoFixtures.ordemServico())

        assertEquals(OrdemServicoFixtures.ID, salva.id.toString())
        assertEquals(2, salva.orcamento.itens.size)

        verify(exactly = 1) { jpaRepositoryMock.save(any()) }
    }

    @Test
    @DisplayName("Dado um id existente, quando buscar por id, então deve retornar a ordem convertida")
    fun givenExistingId_whenSearchingById_thenReturnConvertedOrdem() {
        every { jpaRepositoryMock.findById(id.value) } returns Optional.of(OrdemServicoFixtures.ordemServicoJPA())

        assertEquals(OrdemServicoFixtures.ID, repository.buscarPorId(id)?.id?.toString())

        verify(exactly = 1) { jpaRepositoryMock.findById(id.value) }
    }

    @Test
    @DisplayName("Dado um id inexistente, quando buscar por id, então deve retornar nulo")
    fun givenNonExistentId_whenSearchingById_thenReturnNull() {
        every { jpaRepositoryMock.findById(any<UUID>()) } returns Optional.empty()

        assertNull(repository.buscarPorId(id))
    }

    @Test
    @DisplayName("Dado ordens cadastradas, quando listar todas, então deve converter cada registro")
    fun givenRegisteredOrdens_whenListingAll_thenConvertEachRecord() {
        every { jpaRepositoryMock.findAll() } returns
            listOf(
                OrdemServicoFixtures.ordemServicoJPA(),
                OrdemServicoFixtures.ordemServicoJPA(id = OrdemServicoFixtures.ID_ALTERNATIVO),
            )

        assertEquals(2, repository.listarTodos().size)
    }

    @Test
    @DisplayName("Dado um status, quando buscar por status, então deve delegar a consulta ao repositório JPA")
    fun givenStatus_whenSearchingByStatus_thenDelegateQueryToJpaRepository() {
        every { jpaRepositoryMock.findByStatus(OrdemServicoStatus.RECEBIDA) } returns
            listOf(OrdemServicoFixtures.ordemServicoJPA())

        assertEquals(1, repository.buscarPorStatus(OrdemServicoStatus.RECEBIDA).size)

        verify(exactly = 1) { jpaRepositoryMock.findByStatus(OrdemServicoStatus.RECEBIDA) }
    }

    @Test
    @DisplayName("Dado um cliente, quando buscar por cliente, então deve delegar a consulta ao repositório JPA")
    fun givenCliente_whenSearchingByCliente_thenDelegateQueryToJpaRepository() {
        val ref = ClienteRef.toUUID(OrdemServicoFixtures.CLIENTE_ID)

        every { jpaRepositoryMock.findByClienteId(ref.value) } returns listOf(OrdemServicoFixtures.ordemServicoJPA())

        assertEquals(1, repository.buscarPorCliente(ref).size)

        verify(exactly = 1) { jpaRepositoryMock.findByClienteId(ref.value) }
    }

    @Test
    @DisplayName("Dado um veículo, quando buscar por veículo, então deve delegar a consulta ao repositório JPA")
    fun givenVeiculo_whenSearchingByVeiculo_thenDelegateQueryToJpaRepository() {
        val ref = VeiculoRef.toUUID(OrdemServicoFixtures.VEICULO_ID)

        every { jpaRepositoryMock.findByVeiculoId(ref.value) } returns listOf(OrdemServicoFixtures.ordemServicoJPA())

        assertEquals(1, repository.buscarPorVeiculo(ref).size)

        verify(exactly = 1) { jpaRepositoryMock.findByVeiculoId(ref.value) }
    }

    @Test
    @DisplayName("Dado um intervalo, quando listar por data de abertura, então deve delegar a consulta por período")
    fun givenInterval_whenListingByOpeningDate_thenDelegatePeriodQuery() {
        val inicio = OrdemServicoFixtures.DATA_ABERTURA
        val fim = OrdemServicoFixtures.DATA_FINALIZACAO

        every { jpaRepositoryMock.findByDataAberturaBetween(inicio, fim) } returns
            listOf(OrdemServicoFixtures.ordemServicoJPA())

        assertEquals(1, repository.listarPorDataAberturaEntre(inicio, fim).size)

        verify(exactly = 1) { jpaRepositoryMock.findByDataAberturaBetween(inicio, fim) }
    }

    @Test
    @DisplayName("Dado um id existente, quando deletar, então deve remover e retornar verdadeiro")
    fun givenExistingId_whenDeleting_thenRemoveAndReturnTrue() {
        every { jpaRepositoryMock.existsById(id.value) } returns true
        every { jpaRepositoryMock.deleteById(id.value) } just runs

        assertTrue(repository.deletar(id))

        verify(exactly = 1) { jpaRepositoryMock.deleteById(id.value) }
    }

    @Test
    @DisplayName("Dado um id inexistente, quando deletar, então deve retornar falso sem remover")
    fun givenNonExistentId_whenDeleting_thenReturnFalseWithoutRemoving() {
        every { jpaRepositoryMock.existsById(id.value) } returns false

        assertFalse(repository.deletar(id))

        verify(exactly = 0) { jpaRepositoryMock.deleteById(any<UUID>()) }
    }
}
