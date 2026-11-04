package br.com.fiap.oficina.ordemservico.domain

import br.com.fiap.oficina.ordemservico.OrdemServicoFixtures
import br.com.fiap.oficina.ordemservico.domain.port.cliente.DadosCliente
import br.com.fiap.oficina.ordemservico.domain.port.funcionario.DadosFuncionario
import br.com.fiap.oficina.ordemservico.domain.port.peca.DadosPeca
import br.com.fiap.oficina.ordemservico.domain.port.servico.DadosServico
import br.com.fiap.oficina.ordemservico.domain.port.veiculo.DadosVeiculo
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertFailsWith

@DisplayName("Domínio - Identificadores e referências")
class ReferenciasTest {
    @Test
    @DisplayName("Dado o gerador, quando criar ids, então devem ser distintos")
    fun givenGenerator_whenCreatingIds_thenTheyDiffer() {
        assertNotEquals(OrdemServicoId.generate(), OrdemServicoId.generate())
    }

    @Test
    @DisplayName("Dado um uuid em texto, quando converter, então deve preservar o valor")
    fun givenUuidText_whenConverting_thenPreserveValue() {
        val uuid = UUID.fromString(OrdemServicoFixtures.ID)

        assertEquals(uuid, OrdemServicoId.toUUID(OrdemServicoFixtures.ID).value)
        assertEquals(OrdemServicoFixtures.CLIENTE_ID, ClienteRef.toUUID(OrdemServicoFixtures.CLIENTE_ID).toString())
        assertEquals(
            OrdemServicoFixtures.FUNCIONARIO_ID,
            FuncionarioRef.toUUID(OrdemServicoFixtures.FUNCIONARIO_ID).toString(),
        )
        assertEquals(OrdemServicoFixtures.VEICULO_ID, VeiculoRef.toUUID(OrdemServicoFixtures.VEICULO_ID).toString())
        assertEquals(OrdemServicoFixtures.PECA_ID, ItemReferenciaRef.toUUID(OrdemServicoFixtures.PECA_ID).toString())
    }

    @Test
    @DisplayName("Dado um texto inválido, quando converter, então deve lançar exceção")
    fun givenInvalidText_whenConverting_thenThrowException() {
        assertFailsWith<IllegalArgumentException> { OrdemServicoId.toUUID(OrdemServicoFixtures.ID_INVALIDO) }
        assertFailsWith<IllegalArgumentException> { ClienteRef.toUUID(OrdemServicoFixtures.ID_INVALIDO) }
        assertFailsWith<IllegalArgumentException> { FuncionarioRef.toUUID(OrdemServicoFixtures.ID_INVALIDO) }
        assertFailsWith<IllegalArgumentException> { VeiculoRef.toUUID(OrdemServicoFixtures.ID_INVALIDO) }
        assertFailsWith<IllegalArgumentException> { ItemReferenciaRef.toUUID(OrdemServicoFixtures.ID_INVALIDO) }
    }

    @Test
    @DisplayName("Dado o mesmo valor, quando comparar as referências, então devem ser iguais")
    fun givenSameValue_whenComparingReferences_thenTheyAreEqual() {
        val primeira = ClienteRef.toUUID(OrdemServicoFixtures.CLIENTE_ID)
        val segunda = ClienteRef.toUUID(OrdemServicoFixtures.CLIENTE_ID)

        assertEquals(primeira, segunda)
        assertEquals(primeira.hashCode(), segunda.hashCode())
        assertEquals(primeira, primeira.copy())
        assertEquals(primeira.value, primeira.component1())
    }

    @Test
    @DisplayName("Dado os dados dos contextos externos, quando comparar, então devem respeitar a igualdade estrutural")
    fun givenExternalContextData_whenComparing_thenRespectStructuralEquality() {
        assertEquals(OrdemServicoFixtures.dadosCliente(), OrdemServicoFixtures.dadosCliente())
        assertEquals(OrdemServicoFixtures.dadosFuncionario(), OrdemServicoFixtures.dadosFuncionario())
        assertEquals(OrdemServicoFixtures.dadosVeiculo(), OrdemServicoFixtures.dadosVeiculo())
        assertEquals(OrdemServicoFixtures.dadosPeca(), OrdemServicoFixtures.dadosPeca())
        assertEquals(OrdemServicoFixtures.dadosServico(), OrdemServicoFixtures.dadosServico())
    }

    @Test
    @DisplayName("Dado os dados de cliente e funcionário, quando acessar os membros gerados, então devem responder")
    fun givenClienteAndFuncionarioData_whenAccessingGeneratedMembers_thenRespond() {
        val cliente: DadosCliente = OrdemServicoFixtures.dadosCliente()
        val funcionario: DadosFuncionario = OrdemServicoFixtures.dadosFuncionario()

        assertEquals(cliente.id, cliente.component1())
        assertEquals(cliente.hashCode(), OrdemServicoFixtures.dadosCliente().hashCode())
        assertEquals(cliente, cliente.copy())
        assertTrue(cliente.toString().contains(OrdemServicoFixtures.CLIENTE_ID))

        assertEquals(funcionario.id, funcionario.component1())
        assertEquals(funcionario.hashCode(), OrdemServicoFixtures.dadosFuncionario().hashCode())
        assertEquals(funcionario, funcionario.copy())
        assertTrue(funcionario.toString().contains(OrdemServicoFixtures.FUNCIONARIO_ID))
    }

    @Test
    @DisplayName("Dado os dados de veículo, peça e serviço, quando acessar os membros gerados, então devem responder")
    fun givenVeiculoPecaAndServicoData_whenAccessingGeneratedMembers_thenRespond() {
        val veiculo: DadosVeiculo = OrdemServicoFixtures.dadosVeiculo()
        val peca: DadosPeca = OrdemServicoFixtures.dadosPeca()
        val servico: DadosServico = OrdemServicoFixtures.dadosServico()

        assertEquals(OrdemServicoFixtures.PLACA, veiculo.component2())
        assertEquals(veiculo, veiculo.copy())
        assertEquals(veiculo.hashCode(), OrdemServicoFixtures.dadosVeiculo().hashCode())
        assertTrue(veiculo.toString().contains(OrdemServicoFixtures.PLACA))

        assertEquals(OrdemServicoFixtures.PECA_CODIGO, peca.component2())
        assertEquals(peca, peca.copy())
        assertEquals(peca.hashCode(), OrdemServicoFixtures.dadosPeca().hashCode())
        assertNotEquals(peca, OrdemServicoFixtures.dadosPeca(ativo = false))
        assertTrue(peca.toString().contains(OrdemServicoFixtures.PECA_NOME))

        assertEquals(OrdemServicoFixtures.SERVICO_DESCRICAO, servico.component2())
        assertEquals(servico, servico.copy())
        assertEquals(servico.hashCode(), OrdemServicoFixtures.dadosServico().hashCode())
        assertNotEquals(servico, OrdemServicoFixtures.dadosServico(ativo = false))
        assertTrue(servico.toString().contains(OrdemServicoFixtures.SERVICO_DESCRICAO))
    }

    @Test
    @DisplayName("Dado o enum de tipo de item, quando listar os valores, então deve conter peça e serviço")
    fun givenItemTypeEnum_whenListingValues_thenContainPecaAndServico() {
        assertEquals(
            listOf(TipoItemOrcamento.SERVICO, TipoItemOrcamento.PECA),
            TipoItemOrcamento.entries.toList(),
        )
        assertEquals(TipoItemOrcamento.PECA, TipoItemOrcamento.valueOf("PECA"))
    }
}
