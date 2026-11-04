package br.com.fiap.oficina.ordemservico.application.mapper

import br.com.fiap.oficina.ordemservico.OrdemServicoFixtures
import br.com.fiap.oficina.ordemservico.domain.OrdemServicoStatus
import br.com.fiap.oficina.ordemservico.domain.TipoItemOrcamento
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Aplicação - Mapper de Ordem de Serviço")
class OrdemServicoMapperTest {
    private val mapper = OrdemServicoMapper(ItemOrcamentoMapper())

    @Test
    @DisplayName("Dado uma ordem de serviço, quando converter para response, então deve mapear todos os campos")
    fun givenOrdemServico_whenConvertingToResponse_thenMapAllFields() {
        val response = mapper.toResponse(OrdemServicoFixtures.ordemServico())

        assertEquals(OrdemServicoFixtures.ID, response.id)
        assertEquals(OrdemServicoFixtures.DESCRICAO, response.descricao)
        assertEquals(OrdemServicoStatus.RECEBIDA, response.status)
        assertEquals(OrdemServicoFixtures.FUNCIONARIO_ID, response.funcionarioId)
        assertEquals(OrdemServicoFixtures.CLIENTE_ID, response.clienteId)
        assertEquals(OrdemServicoFixtures.VEICULO_ID, response.veiculoId)
        assertEquals(OrdemServicoFixtures.DATA_ABERTURA, response.dataAbertura)
        assertNull(response.dataInicioExecucao)
        assertNull(response.dataFinalizacao)
        assertEquals(2, response.itens.size)
    }

    @Test
    @DisplayName("Dado uma ordem finalizada, quando converter para response, então deve mapear as datas de execução")
    fun givenFinishedOrdem_whenConvertingToResponse_thenMapExecutionDates() {
        val response = mapper.toResponse(OrdemServicoFixtures.ordemServicoFinalizada())

        assertEquals(OrdemServicoFixtures.DATA_INICIO_EXECUCAO, response.dataInicioExecucao)
        assertEquals(OrdemServicoFixtures.DATA_FINALIZACAO, response.dataFinalizacao)
        assertEquals(OrdemServicoStatus.FINALIZADA, response.status)
    }

    @Test
    @DisplayName("Dado uma ordem sem itens, quando converter para response, então deve retornar lista vazia")
    fun givenOrdemWithoutItems_whenConvertingToResponse_thenReturnEmptyList() {
        val ordemServico = OrdemServicoFixtures.ordemServico(orcamento = OrdemServicoFixtures.orcamento(emptyList()))

        assertTrue(mapper.toResponse(ordemServico).itens.isEmpty())
    }

    @Test
    @DisplayName("Dado uma ordem com peça e serviço, quando converter, então deve preservar a ordem dos itens")
    fun givenOrdemWithPecaAndServico_whenConverting_thenKeepItemOrder() {
        val itens = mapper.toResponse(OrdemServicoFixtures.ordemServico()).itens

        assertEquals(TipoItemOrcamento.PECA, itens.first().tipo)
        assertEquals(TipoItemOrcamento.SERVICO, itens.last().tipo)
    }
}
