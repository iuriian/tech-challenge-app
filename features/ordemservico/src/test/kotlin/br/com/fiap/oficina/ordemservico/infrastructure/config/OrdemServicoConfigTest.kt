package br.com.fiap.oficina.ordemservico.infrastructure.config

import br.com.fiap.oficina.cliente.application.port.ClienteQueryPort
import br.com.fiap.oficina.funcionario.application.port.FuncionarioQueryPort
import br.com.fiap.oficina.ordemservico.OrdemServicoFixtures
import br.com.fiap.oficina.ordemservico.domain.ClienteRef
import br.com.fiap.oficina.ordemservico.domain.FuncionarioRef
import br.com.fiap.oficina.ordemservico.domain.ItemReferenciaRef
import br.com.fiap.oficina.ordemservico.domain.VeiculoRef
import br.com.fiap.oficina.ordemservico.infrastructure.persistence.OrdemServicoJPARepository
import br.com.fiap.oficina.ordemservico.infrastructure.persistence.OrdemServicoRepositoryImpl
import br.com.fiap.oficina.ordemservico.infrastructure.persistence.SequenciaOrdemServicoImpl
import br.com.fiap.oficina.peca.application.port.PecaQueryPort
import br.com.fiap.oficina.servico.application.port.ServicoQueryPort
import br.com.fiap.oficina.veiculo.application.port.VeiculoQueryPort
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import jakarta.persistence.EntityManager
import jakarta.persistence.Query
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.UUID

@DisplayName("Infraestrutura - Configuração de Ordem de Serviço")
class OrdemServicoConfigTest {
    private val config = OrdemServicoConfig()

    @Test
    @DisplayName("Dado a configuração, quando criar os mappers, então deve retornar novas instâncias")
    fun givenConfig_whenCreatingMappers_thenReturnNewInstances() {
        assertNotSame(config.itemOrcamentoJPAMapper(), config.itemOrcamentoJPAMapper())
        assertNotSame(config.ordemServicoClienteMapper(), config.ordemServicoClienteMapper())
        assertNotSame(config.ordemServicoFuncionarioMapper(), config.ordemServicoFuncionarioMapper())
        assertNotSame(config.ordemServicoVeiculoMapper(), config.ordemServicoVeiculoMapper())
        assertNotSame(config.ordemServicoPecaMapper(), config.ordemServicoPecaMapper())
        assertNotSame(config.ordemServicoServicoMapper(), config.ordemServicoServicoMapper())
        assertNotSame(
            config.ordemServicoJPAMapper(config.itemOrcamentoJPAMapper()),
            config.ordemServicoJPAMapper(config.itemOrcamentoJPAMapper()),
        )
    }

    @Test
    @DisplayName("Dado as dependências, quando criar o repositório, então deve delegar ao repositório JPA")
    fun givenDependencies_whenCreatingRepository_thenDelegateToJpaRepository() {
        val jpaRepositoryMock = mockk<OrdemServicoJPARepository>()
        every { jpaRepositoryMock.findAll() } returns emptyList()

        val repository =
            config.ordemServicoRepository(
                jpaRepositoryMock,
                config.ordemServicoJPAMapper(config.itemOrcamentoJPAMapper()),
            )

        assertInstanceOf(OrdemServicoRepositoryImpl::class.java, repository)

        repository.listarTodos()

        verify(exactly = 1) { jpaRepositoryMock.findAll() }
    }

    @Test
    @DisplayName("Dado o entity manager, quando criar a sequência, então deve consultar o nextval")
    fun givenEntityManager_whenCreatingSequence_thenQueryNextval() {
        val entityManagerMock = mockk<EntityManager>()
        val queryMock = mockk<Query>()

        every { entityManagerMock.createNativeQuery(any<String>()) } returns queryMock
        every { queryMock.singleResult } returns OrdemServicoFixtures.SEQUENCIAL

        val sequencia = config.sequenciaOrdemServico(entityManagerMock)

        assertInstanceOf(SequenciaOrdemServicoImpl::class.java, sequencia)
        assertEquals(OrdemServicoFixtures.SEQUENCIAL, sequencia.proximoValor())
    }

    @Test
    @DisplayName("Dado os query ports, quando criar os adaptadores, então devem delegar a consulta")
    fun givenQueryPorts_whenCreatingAdapters_thenDelegateQuery() {
        val clienteQueryPortMock = mockk<ClienteQueryPort>()
        val funcionarioQueryPortMock = mockk<FuncionarioQueryPort>()
        val veiculoQueryPortMock = mockk<VeiculoQueryPort>()
        val pecaQueryPortMock = mockk<PecaQueryPort>()
        val servicoQueryPortMock = mockk<ServicoQueryPort>()

        every { clienteQueryPortMock.buscarPorId(any<UUID>()) } returns OrdemServicoFixtures.clienteQueryDto()
        every { funcionarioQueryPortMock.buscarPorId(any<UUID>()) } returns OrdemServicoFixtures.funcionarioQueryDto()
        every { veiculoQueryPortMock.buscarPorId(any<UUID>()) } returns OrdemServicoFixtures.veiculoQueryDto()
        every { pecaQueryPortMock.buscarPorId(any<UUID>()) } returns OrdemServicoFixtures.pecaQueryDto()
        every { servicoQueryPortMock.buscarPorId(any<UUID>()) } returns OrdemServicoFixtures.servicoQueryDto()

        val cliente =
            config.consultarClienteAdapter(clienteQueryPortMock, config.ordemServicoClienteMapper())
        val funcionario =
            config.consultarFuncionarioAdapter(funcionarioQueryPortMock, config.ordemServicoFuncionarioMapper())
        val veiculo =
            config.consultarVeiculoAdapter(veiculoQueryPortMock, config.ordemServicoVeiculoMapper())
        val peca = config.consultarPecaAdapter(pecaQueryPortMock, config.ordemServicoPecaMapper())
        val servico = config.consultarServicoAdapter(servicoQueryPortMock, config.ordemServicoServicoMapper())

        assertEquals(
            ClienteRef.toUUID(OrdemServicoFixtures.CLIENTE_ID),
            cliente.buscarPorId(ClienteRef.toUUID(OrdemServicoFixtures.CLIENTE_ID))?.id,
        )
        assertEquals(
            FuncionarioRef.toUUID(OrdemServicoFixtures.FUNCIONARIO_ID),
            funcionario.buscarPorId(FuncionarioRef.toUUID(OrdemServicoFixtures.FUNCIONARIO_ID))?.id,
        )
        assertEquals(
            OrdemServicoFixtures.PLACA,
            veiculo.buscarPorId(VeiculoRef.toUUID(OrdemServicoFixtures.VEICULO_ID))?.placa,
        )
        assertEquals(
            OrdemServicoFixtures.PECA_CODIGO,
            peca.buscarPorId(ItemReferenciaRef.toUUID(OrdemServicoFixtures.PECA_ID))?.codigo,
        )
        assertEquals(
            OrdemServicoFixtures.SERVICO_DESCRICAO,
            servico.buscarPorId(ItemReferenciaRef.toUUID(OrdemServicoFixtures.SERVICO_ID))?.descricao,
        )
    }
}
