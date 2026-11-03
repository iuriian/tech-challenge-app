package br.com.fiap.oficina.ordemservico.infrastructure.acl

import br.com.fiap.oficina.cliente.application.port.ClienteQueryPort
import br.com.fiap.oficina.funcionario.application.port.FuncionarioQueryPort
import br.com.fiap.oficina.ordemservico.OrdemServicoFixtures
import br.com.fiap.oficina.ordemservico.domain.ClienteRef
import br.com.fiap.oficina.ordemservico.domain.FuncionarioRef
import br.com.fiap.oficina.ordemservico.domain.ItemReferenciaRef
import br.com.fiap.oficina.ordemservico.domain.VeiculoRef
import br.com.fiap.oficina.ordemservico.infrastructure.acl.cliente.ClienteMapper
import br.com.fiap.oficina.ordemservico.infrastructure.acl.cliente.ConsultarClienteAdapter
import br.com.fiap.oficina.ordemservico.infrastructure.acl.funcionario.ConsultarFuncionarioAdapter
import br.com.fiap.oficina.ordemservico.infrastructure.acl.funcionario.FuncionarioMapper
import br.com.fiap.oficina.ordemservico.infrastructure.acl.peca.ConsultarPecaAdapter
import br.com.fiap.oficina.ordemservico.infrastructure.acl.peca.PecaMapper
import br.com.fiap.oficina.ordemservico.infrastructure.acl.servico.ConsultarServicoAdapter
import br.com.fiap.oficina.ordemservico.infrastructure.acl.servico.ServicoMapper
import br.com.fiap.oficina.ordemservico.infrastructure.acl.veiculo.ConsultarVeiculoAdapter
import br.com.fiap.oficina.ordemservico.infrastructure.acl.veiculo.VeiculoMapper
import br.com.fiap.oficina.peca.application.port.PecaQueryPort
import br.com.fiap.oficina.servico.application.port.ServicoQueryPort
import br.com.fiap.oficina.veiculo.application.port.VeiculoQueryPort
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertFailsWith

@DisplayName("Infraestrutura - Camada anticorrupção da Ordem de Serviço")
class AclOrdemServicoTest {
    @Nested
    @DisplayName("Cliente")
    inner class Cliente {
        private val queryPortMock = mockk<ClienteQueryPort>()
        private val adapter = ConsultarClienteAdapter(queryPortMock, ClienteMapper())

        @Test
        @DisplayName("Dado um cliente cadastrado, quando buscar por id, então deve retornar a referência")
        fun givenRegisteredCliente_whenSearchingById_thenReturnReference() {
            val ref = ClienteRef.toUUID(OrdemServicoFixtures.CLIENTE_ID)

            every { queryPortMock.buscarPorId(ref.value) } returns OrdemServicoFixtures.clienteQueryDto()

            assertEquals(ref, adapter.buscarPorId(ref)?.id)

            verify(exactly = 1) { queryPortMock.buscarPorId(ref.value) }
        }

        @Test
        @DisplayName("Dado um cliente não cadastrado, quando buscar por id, então deve retornar nulo")
        fun givenUnregisteredCliente_whenSearchingById_thenReturnNull() {
            every { queryPortMock.buscarPorId(any<UUID>()) } returns null

            assertNull(adapter.buscarPorId(ClienteRef.toUUID(OrdemServicoFixtures.CLIENTE_ID)))
        }

        @Test
        @DisplayName("Dado um dto com id inválido, quando converter, então deve lançar exceção")
        fun givenDtoWithInvalidId_whenConverting_thenThrowException() {
            assertFailsWith<IllegalArgumentException> {
                ClienteMapper().toProviderResponse(
                    OrdemServicoFixtures.clienteQueryDto(id = OrdemServicoFixtures.ID_INVALIDO),
                )
            }
        }
    }

    @Nested
    @DisplayName("Funcionário")
    inner class Funcionario {
        private val queryPortMock = mockk<FuncionarioQueryPort>()
        private val adapter = ConsultarFuncionarioAdapter(queryPortMock, FuncionarioMapper())

        @Test
        @DisplayName("Dado um funcionário cadastrado, quando buscar por id, então deve retornar a referência")
        fun givenRegisteredFuncionario_whenSearchingById_thenReturnReference() {
            val ref = FuncionarioRef.toUUID(OrdemServicoFixtures.FUNCIONARIO_ID)

            every { queryPortMock.buscarPorId(ref.value) } returns OrdemServicoFixtures.funcionarioQueryDto()

            assertEquals(ref, adapter.buscarPorId(ref)?.id)
        }

        @Test
        @DisplayName("Dado um funcionário não cadastrado, quando buscar por id, então deve retornar nulo")
        fun givenUnregisteredFuncionario_whenSearchingById_thenReturnNull() {
            every { queryPortMock.buscarPorId(any<UUID>()) } returns null

            assertNull(adapter.buscarPorId(FuncionarioRef.toUUID(OrdemServicoFixtures.FUNCIONARIO_ID)))
        }
    }

    @Nested
    @DisplayName("Veículo")
    inner class Veiculo {
        private val queryPortMock = mockk<VeiculoQueryPort>()
        private val adapter = ConsultarVeiculoAdapter(queryPortMock, VeiculoMapper())

        @Test
        @DisplayName("Dado um veículo cadastrado, quando buscar por id, então deve retornar a referência e a placa")
        fun givenRegisteredVeiculo_whenSearchingById_thenReturnReferenceAndPlate() {
            val ref = VeiculoRef.toUUID(OrdemServicoFixtures.VEICULO_ID)

            every { queryPortMock.buscarPorId(ref.value) } returns OrdemServicoFixtures.veiculoQueryDto()

            val dados = adapter.buscarPorId(ref)

            assertEquals(ref, dados?.id)
            assertEquals(OrdemServicoFixtures.PLACA, dados?.placa)
        }

        @Test
        @DisplayName("Dado um veículo não cadastrado, quando buscar por id, então deve retornar nulo")
        fun givenUnregisteredVeiculo_whenSearchingById_thenReturnNull() {
            every { queryPortMock.buscarPorId(any<UUID>()) } returns null

            assertNull(adapter.buscarPorId(VeiculoRef.toUUID(OrdemServicoFixtures.VEICULO_ID)))
        }
    }

    @Nested
    @DisplayName("Peça")
    inner class Peca {
        private val queryPortMock = mockk<PecaQueryPort>()
        private val adapter = ConsultarPecaAdapter(queryPortMock, PecaMapper())

        @Test
        @DisplayName("Dado uma peça cadastrada, quando buscar por id, então deve retornar os dados do catálogo")
        fun givenRegisteredPeca_whenSearchingById_thenReturnCatalogData() {
            val ref = ItemReferenciaRef.toUUID(OrdemServicoFixtures.PECA_ID)

            every { queryPortMock.buscarPorId(ref.value) } returns OrdemServicoFixtures.pecaQueryDto()

            val dados = adapter.buscarPorId(ref)

            assertEquals(ref, dados?.id)
            assertEquals(OrdemServicoFixtures.PECA_CODIGO, dados?.codigo)
            assertEquals(OrdemServicoFixtures.PECA_NOME, dados?.nome)
            assertEquals(OrdemServicoFixtures.PECA_PRECO, dados?.precoVenda)
            assertTrue(dados!!.ativo)
        }

        @Test
        @DisplayName("Dado uma peça inativa, quando buscar por id, então deve propagar o estado inativo")
        fun givenInactivePeca_whenSearchingById_thenPropagateInactiveState() {
            every { queryPortMock.buscarPorId(any<UUID>()) } returns
                OrdemServicoFixtures.pecaQueryDto(ativo = false)

            val dados = adapter.buscarPorId(ItemReferenciaRef.toUUID(OrdemServicoFixtures.PECA_ID))

            assertFalse(dados!!.ativo)
        }

        @Test
        @DisplayName("Dado uma peça não cadastrada, quando buscar por id, então deve retornar nulo")
        fun givenUnregisteredPeca_whenSearchingById_thenReturnNull() {
            every { queryPortMock.buscarPorId(any<UUID>()) } returns null

            assertNull(adapter.buscarPorId(ItemReferenciaRef.toUUID(OrdemServicoFixtures.PECA_ID)))
        }
    }

    @Nested
    @DisplayName("Serviço")
    inner class Servico {
        private val queryPortMock = mockk<ServicoQueryPort>()
        private val adapter = ConsultarServicoAdapter(queryPortMock, ServicoMapper())

        @Test
        @DisplayName("Dado um serviço cadastrado, quando buscar por id, então deve retornar os dados do catálogo")
        fun givenRegisteredServico_whenSearchingById_thenReturnCatalogData() {
            val ref = ItemReferenciaRef.toUUID(OrdemServicoFixtures.SERVICO_ID)

            every { queryPortMock.buscarPorId(ref.value) } returns OrdemServicoFixtures.servicoQueryDto()

            val dados = adapter.buscarPorId(ref)

            assertEquals(ref, dados?.id)
            assertEquals(OrdemServicoFixtures.SERVICO_DESCRICAO, dados?.descricao)
            assertEquals(OrdemServicoFixtures.SERVICO_VALOR, dados?.valor)
            assertTrue(dados!!.ativo)
        }

        @Test
        @DisplayName("Dado um serviço inativo, quando buscar por id, então deve propagar o estado inativo")
        fun givenInactiveServico_whenSearchingById_thenPropagateInactiveState() {
            every { queryPortMock.buscarPorId(any<UUID>()) } returns
                OrdemServicoFixtures.servicoQueryDto(ativo = false)

            val dados = adapter.buscarPorId(ItemReferenciaRef.toUUID(OrdemServicoFixtures.SERVICO_ID))

            assertFalse(dados!!.ativo)
        }

        @Test
        @DisplayName("Dado um serviço não cadastrado, quando buscar por id, então deve retornar nulo")
        fun givenUnregisteredServico_whenSearchingById_thenReturnNull() {
            every { queryPortMock.buscarPorId(any<UUID>()) } returns null

            assertNull(adapter.buscarPorId(ItemReferenciaRef.toUUID(OrdemServicoFixtures.SERVICO_ID)))
        }
    }
}
