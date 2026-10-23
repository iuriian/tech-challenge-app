package br.com.fiap.oficina.veiculo.infrastructure.config

import br.com.fiap.oficina.cliente.application.port.ClienteQueryPort
import br.com.fiap.oficina.veiculo.VeiculoFixtures
import br.com.fiap.oficina.veiculo.domain.ProprietarioRef
import br.com.fiap.oficina.veiculo.infrastructure.persistence.VeiculoJPARepository
import br.com.fiap.oficina.veiculo.infrastructure.persistence.VeiculoRepositoryImpl
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.UUID

@DisplayName("Infraestrutura - Configuração de Veículo")
class VeiculoConfigTest {
    private val config = VeiculoConfig()

    @Test
    @DisplayName("Dado a configuração, quando criar os mappers, então deve retornar novas instâncias")
    fun givenConfig_whenCreatingMappers_thenReturnNewInstances() {
        assertNotSame(config.veiculoJpaMapper(), config.veiculoJpaMapper())
        assertNotSame(config.proprietarioMapper(), config.proprietarioMapper())
    }

    @Test
    @DisplayName("Dado as dependências, quando criar o repositório, então deve delegar ao repositório JPA")
    fun givenDependencies_whenCreatingRepository_thenDelegateToJpaRepository() {
        val jpaRepositoryMock = mockk<VeiculoJPARepository>()
        every { jpaRepositoryMock.findAll() } returns emptyList()

        val repository = config.veiculoRepository(jpaRepositoryMock, config.veiculoJpaMapper())

        assertInstanceOf(VeiculoRepositoryImpl::class.java, repository)

        repository.listarTodos()

        verify(exactly = 1) { jpaRepositoryMock.findAll() }
    }

    @Test
    @DisplayName("Dado as dependências, quando criar o adaptador, então deve delegar à porta de consulta de cliente")
    fun givenDependencies_whenCreatingAdapter_thenDelegateToClienteQueryPort() {
        val queryPortMock = mockk<ClienteQueryPort>()
        val ref = ProprietarioRef.toUUID(VeiculoFixtures.PROPRIETARIO_ID)

        every { queryPortMock.buscarPorId(any<UUID>()) } returns VeiculoFixtures.clienteQueryDto()

        val adapter = config.consultarProprietarioAdapter(queryPortMock, config.proprietarioMapper())

        assertEquals(VeiculoFixtures.PROPRIETARIO_NOME, adapter.buscarPorId(ref)?.nome)

        verify(exactly = 1) { queryPortMock.buscarPorId(ref.value) }
    }
}
