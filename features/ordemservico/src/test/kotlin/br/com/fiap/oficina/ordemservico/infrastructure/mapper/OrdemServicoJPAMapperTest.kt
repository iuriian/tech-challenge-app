package br.com.fiap.oficina.ordemservico.infrastructure.mapper

import br.com.fiap.oficina.ordemservico.OrdemServicoFixtures
import br.com.fiap.oficina.ordemservico.domain.OrdemServicoStatus
import br.com.fiap.oficina.shared.domain.exception.BusinessRuleException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertFailsWith

@DisplayName("Infraestrutura - Mapper JPA de Ordem de Serviço")
class OrdemServicoJPAMapperTest {
    private val mapper = OrdemServicoJPAMapper(ItemOrcamentoJPAMapper())

    @Test
    @DisplayName("Dado uma ordem de domínio, quando converter para JPA, então deve mapear todos os campos")
    fun givenDomainOrdem_whenConvertingToJPA_thenMapAllFields() {
        val jpa = mapper.toJPA(OrdemServicoFixtures.ordemServico())

        assertEquals(UUID.fromString(OrdemServicoFixtures.ID), jpa.id)
        assertEquals(OrdemServicoFixtures.numero().valor, jpa.osNumber)
        assertEquals(OrdemServicoFixtures.DESCRICAO, jpa.descricao)
        assertEquals(OrdemServicoStatus.RECEBIDA, jpa.status)
        assertEquals(UUID.fromString(OrdemServicoFixtures.FUNCIONARIO_ID), jpa.funcionarioId)
        assertEquals(UUID.fromString(OrdemServicoFixtures.CLIENTE_ID), jpa.clienteId)
        assertEquals(UUID.fromString(OrdemServicoFixtures.VEICULO_ID), jpa.veiculoId)
        assertEquals(OrdemServicoFixtures.PRAZO.toMinutes(), jpa.prazoMinutos)
        assertEquals(OrdemServicoFixtures.DATA_ABERTURA, jpa.dataAbertura)
        assertEquals(2, jpa.itens.size)
        assertNull(jpa.dataInicioExecucao)
        assertNull(jpa.dataFinalizacao)
    }

    @Test
    @DisplayName("Dado uma ordem sem prazo, quando converter para JPA, então deve manter os minutos nulos")
    fun givenOrdemWithoutDeadline_whenConvertingToJPA_thenKeepNullMinutes() {
        assertNull(mapper.toJPA(OrdemServicoFixtures.ordemServico(prazo = null)).prazoMinutos)
    }

    @Test
    @DisplayName("Dado uma entidade JPA, quando converter para domínio, então deve mapear todos os campos")
    fun givenJPAEntity_whenConvertingToDomain_thenMapAllFields() {
        val ordemServico = mapper.toDomain(OrdemServicoFixtures.ordemServicoJPA())

        assertEquals(OrdemServicoFixtures.ID, ordemServico.id.toString())
        assertEquals(OrdemServicoFixtures.numero(), ordemServico.osNumber)
        assertEquals(OrdemServicoFixtures.DESCRICAO, ordemServico.descricao)
        assertEquals(OrdemServicoStatus.RECEBIDA, ordemServico.status)
        assertEquals(OrdemServicoFixtures.FUNCIONARIO_ID, ordemServico.funcionarioRef.toString())
        assertEquals(OrdemServicoFixtures.CLIENTE_ID, ordemServico.clienteRef.toString())
        assertEquals(OrdemServicoFixtures.VEICULO_ID, ordemServico.veiculoRef.toString())
        assertEquals(OrdemServicoFixtures.PRAZO, ordemServico.prazo)
        assertEquals(OrdemServicoFixtures.DATA_ABERTURA, ordemServico.dataAbertura)
        assertEquals(1, ordemServico.orcamento.itens.size)
    }

    @Test
    @DisplayName("Dado uma entidade JPA finalizada, quando converter para domínio, então deve mapear as datas")
    fun givenFinishedJPAEntity_whenConvertingToDomain_thenMapDates() {
        val ordemServico =
            mapper.toDomain(
                OrdemServicoFixtures.ordemServicoJPA(
                    status = OrdemServicoStatus.FINALIZADA,
                    dataInicioExecucao = OrdemServicoFixtures.DATA_INICIO_EXECUCAO,
                    dataFinalizacao = OrdemServicoFixtures.DATA_FINALIZACAO,
                ),
            )

        assertEquals(OrdemServicoFixtures.DATA_INICIO_EXECUCAO, ordemServico.dataInicioExecucao)
        assertEquals(OrdemServicoFixtures.DATA_FINALIZACAO, ordemServico.dataFinalizacao)
        assertEquals(OrdemServicoFixtures.MINUTOS_EXECUCAO, ordemServico.tempoExecucaoEmMinutos())
    }

    @Test
    @DisplayName("Dado uma entidade JPA sem prazo, quando converter para domínio, então deve manter o prazo nulo")
    fun givenJPAEntityWithoutDeadline_whenConvertingToDomain_thenKeepNullDeadline() {
        assertNull(mapper.toDomain(OrdemServicoFixtures.ordemServicoJPA(prazoMinutos = null)).prazo)
    }

    @Test
    @DisplayName("Dado uma entidade JPA sem itens, quando converter para domínio, então deve retornar orçamento vazio")
    fun givenJPAEntityWithoutItems_whenConvertingToDomain_thenReturnEmptyBudget() {
        val ordemServico = mapper.toDomain(OrdemServicoFixtures.ordemServicoJPA(itens = emptyList()))

        assertEquals(0, ordemServico.orcamento.itens.size)
    }

    @Test
    @DisplayName("Dado uma entidade JPA sem número de negócio, quando converter, então deve lançar exceção")
    fun givenJPAEntityWithoutBusinessNumber_whenConverting_thenThrowException() {
        val exception =
            assertFailsWith<BusinessRuleException> {
                mapper.toDomain(OrdemServicoFixtures.ordemServicoJPA(osNumber = null))
            }

        assertEquals(
            "Ordem de serviço sem número de negócio, id: ${OrdemServicoFixtures.ID}",
            exception.message,
        )
    }

    @Test
    @DisplayName("Dado uma ordem de domínio, quando converter nos dois sentidos, então deve preservar os dados")
    fun givenDomainOrdem_whenRoundTripping_thenPreserveData() {
        val original = OrdemServicoFixtures.ordemServico()

        assertEquals(original, mapper.toDomain(mapper.toJPA(original)))
    }
}
