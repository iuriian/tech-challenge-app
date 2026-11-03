package br.com.fiap.oficina.ordemservico.infrastructure.mapper

import br.com.fiap.oficina.ordemservico.OrdemServicoFixtures
import br.com.fiap.oficina.ordemservico.domain.TipoItemOrcamento
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.UUID

@DisplayName("Infraestrutura - Mapper JPA de Item do Orçamento")
class ItemOrcamentoJPAMapperTest {
    private val mapper = ItemOrcamentoJPAMapper()

    @Test
    @DisplayName("Dado um item de domínio, quando converter para JPA, então deve mapear todos os campos")
    fun givenDomainItem_whenConvertingToJPA_thenMapAllFields() {
        val jpa = mapper.toJPA(OrdemServicoFixtures.itemPeca())

        assertEquals(TipoItemOrcamento.PECA, jpa.tipo)
        assertEquals(UUID.fromString(OrdemServicoFixtures.PECA_ID), jpa.referenciaId)
        assertEquals(OrdemServicoFixtures.PECA_NOME, jpa.descricao)
        assertEquals(OrdemServicoFixtures.PECA_PRECO, jpa.valorUnitario)
        assertEquals(OrdemServicoFixtures.QUANTIDADE_PECA, jpa.quantidade)
        assertEquals(OrdemServicoFixtures.PECA_CODIGO, jpa.codigoReferencia)
        assertNull(jpa.id)
    }

    @Test
    @DisplayName("Dado um item de serviço, quando converter para JPA, então deve manter o código nulo")
    fun givenServicoItem_whenConvertingToJPA_thenKeepNullCode() {
        val jpa = mapper.toJPA(OrdemServicoFixtures.itemServico())

        assertEquals(TipoItemOrcamento.SERVICO, jpa.tipo)
        assertNull(jpa.codigoReferencia)
    }

    @Test
    @DisplayName("Dado uma entidade JPA, quando converter para domínio, então deve mapear todos os campos")
    fun givenJPAEntity_whenConvertingToDomain_thenMapAllFields() {
        val item = mapper.toDomain(OrdemServicoFixtures.itemOrcamentoJPA())

        assertEquals(TipoItemOrcamento.PECA, item.tipo)
        assertEquals(OrdemServicoFixtures.PECA_ID, item.referenciaId.toString())
        assertEquals(OrdemServicoFixtures.PECA_NOME, item.descricao)
        assertEquals(OrdemServicoFixtures.PECA_PRECO, item.valorUnitario)
        assertEquals(OrdemServicoFixtures.PECA_CODIGO, item.codigoReferencia)
    }

    @Test
    @DisplayName("Dado um item de domínio, quando converter nos dois sentidos, então deve preservar os dados")
    fun givenDomainItem_whenRoundTripping_thenPreserveData() {
        val original = OrdemServicoFixtures.itemPeca()

        assertEquals(original, mapper.toDomain(mapper.toJPA(original)))
    }
}
