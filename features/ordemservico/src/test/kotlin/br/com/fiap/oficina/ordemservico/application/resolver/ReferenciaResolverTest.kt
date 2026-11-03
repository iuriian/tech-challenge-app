package br.com.fiap.oficina.ordemservico.application.resolver

import br.com.fiap.oficina.ordemservico.OrdemServicoFixtures
import br.com.fiap.oficina.ordemservico.domain.ClienteRef
import br.com.fiap.oficina.ordemservico.domain.FuncionarioRef
import br.com.fiap.oficina.ordemservico.domain.VeiculoRef
import br.com.fiap.oficina.ordemservico.domain.port.cliente.ConsultarCliente
import br.com.fiap.oficina.ordemservico.domain.port.funcionario.ConsultarFuncionario
import br.com.fiap.oficina.ordemservico.domain.port.veiculo.ConsultarVeiculo
import br.com.fiap.oficina.shared.domain.exception.BusinessRuleException
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

@DisplayName("Aplicação - Resolver de referências da Ordem de Serviço")
class ReferenciaResolverTest {
    private val consultarClienteMock = mockk<ConsultarCliente>()
    private val consultarFuncionarioMock = mockk<ConsultarFuncionario>()
    private val consultarVeiculoMock = mockk<ConsultarVeiculo>()
    private val resolver =
        ReferenciaResolver(
            consultarClienteMock,
            consultarFuncionarioMock,
            consultarVeiculoMock,
        )

    @Test
    @DisplayName("Dado todas as referências existentes, quando resolver, então deve retornar as referências")
    fun givenAllExistingReferences_whenResolving_thenReturnReferences() {
        referenciasExistentes()

        val referencias = resolver.resolver(OrdemServicoFixtures.request())

        assertEquals(ClienteRef.toUUID(OrdemServicoFixtures.CLIENTE_ID), referencias.clienteRef)
        assertEquals(FuncionarioRef.toUUID(OrdemServicoFixtures.FUNCIONARIO_ID), referencias.funcionarioRef)
        assertEquals(VeiculoRef.toUUID(OrdemServicoFixtures.VEICULO_ID), referencias.veiculoRef)

        verify(exactly = 1) { consultarClienteMock.buscarPorId(any()) }
        verify(exactly = 1) { consultarFuncionarioMock.buscarPorId(any()) }
        verify(exactly = 1) { consultarVeiculoMock.buscarPorId(any()) }
    }

    @Test
    @DisplayName("Dado um cliente inexistente, quando resolver, então deve lançar exceção de regra de negócio")
    fun givenNonExistentCliente_whenResolving_thenThrowBusinessRuleException() {
        referenciasExistentes()
        every { consultarClienteMock.buscarPorId(any()) } returns null

        val exception =
            assertFailsWith<BusinessRuleException> {
                resolver.resolver(OrdemServicoFixtures.request())
            }

        assertEquals("Cliente não encontrado com o id: ${OrdemServicoFixtures.CLIENTE_ID}", exception.message)

        verify(exactly = 0) { consultarFuncionarioMock.buscarPorId(any()) }
    }

    @Test
    @DisplayName("Dado um funcionário inexistente, quando resolver, então deve lançar exceção de regra de negócio")
    fun givenNonExistentFuncionario_whenResolving_thenThrowBusinessRuleException() {
        referenciasExistentes()
        every { consultarFuncionarioMock.buscarPorId(any()) } returns null

        val exception =
            assertFailsWith<BusinessRuleException> {
                resolver.resolver(OrdemServicoFixtures.request())
            }

        assertEquals("Funcionário não encontrado com o id: ${OrdemServicoFixtures.FUNCIONARIO_ID}", exception.message)

        verify(exactly = 0) { consultarVeiculoMock.buscarPorId(any()) }
    }

    @Test
    @DisplayName("Dado um veículo inexistente, quando resolver, então deve lançar exceção de regra de negócio")
    fun givenNonExistentVeiculo_whenResolving_thenThrowBusinessRuleException() {
        referenciasExistentes()
        every { consultarVeiculoMock.buscarPorId(any()) } returns null

        val exception =
            assertFailsWith<BusinessRuleException> {
                resolver.resolver(OrdemServicoFixtures.request())
            }

        assertEquals("Veículo não encontrado com o id: ${OrdemServicoFixtures.VEICULO_ID}", exception.message)
    }

    @Test
    @DisplayName("Dado um id inválido, quando resolver, então deve lançar exceção sem consultar os contextos")
    fun givenInvalidId_whenResolving_thenThrowExceptionWithoutQueryingContexts() {
        assertFailsWith<IllegalArgumentException> {
            resolver.resolver(OrdemServicoFixtures.request(clienteId = OrdemServicoFixtures.ID_INVALIDO))
        }

        verify(exactly = 0) { consultarClienteMock.buscarPorId(any()) }
    }

    @Test
    @DisplayName("Dado as mesmas referências, quando comparar, então devem respeitar a igualdade estrutural")
    fun givenSameReferences_whenComparing_thenRespectStructuralEquality() {
        referenciasExistentes()

        val primeira = resolver.resolver(OrdemServicoFixtures.request())
        val segunda = resolver.resolver(OrdemServicoFixtures.request())

        assertEquals(primeira, segunda)
        assertEquals(primeira.hashCode(), segunda.hashCode())
        assertEquals(primeira, primeira.copy())
        assertEquals(primeira.clienteRef, primeira.component1())
        assertEquals(true, primeira.toString().contains(OrdemServicoFixtures.CLIENTE_ID))
    }

    private fun referenciasExistentes() {
        every { consultarClienteMock.buscarPorId(any()) } returns OrdemServicoFixtures.dadosCliente()
        every { consultarFuncionarioMock.buscarPorId(any()) } returns OrdemServicoFixtures.dadosFuncionario()
        every { consultarVeiculoMock.buscarPorId(any()) } returns OrdemServicoFixtures.dadosVeiculo()
    }
}
