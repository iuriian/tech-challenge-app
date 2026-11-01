package br.com.fiap.oficina.servico.application.mapper

import br.com.fiap.oficina.servico.ServicoFixtures
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

@DisplayName("Aplicação - Mapper de Serviço")
class ServicoMapperTest {
    private val mapper = ServicoMapper()

    @Test
    @DisplayName("Dado um request, quando converter para domínio, então deve mapear os campos e nascer ativo")
    fun givenRequest_whenConvertingToDomain_thenMapFieldsAndStartActive() {
        val servico = mapper.toDomain(ServicoFixtures.request())

        assertEquals(ServicoFixtures.DESCRICAO, servico.descricao)
        assertEquals(ServicoFixtures.VALOR, servico.valor)
        assertTrue(servico.ativo)
    }

    @ParameterizedTest(name = "ativo {0}")
    @ValueSource(booleans = [true, false])
    @DisplayName("Dado um serviço de domínio, quando converter para response, então deve mapear todos os campos")
    fun givenDomainServico_whenConvertingToResponse_thenMapAllFields(ativo: Boolean) {
        val servico = ServicoFixtures.servico(ativo = ativo)

        val response = mapper.toResponse(servico)

        assertEquals(ServicoFixtures.ID_1, response.id)
        assertEquals(ServicoFixtures.DESCRICAO, response.descricao)
        assertEquals(ServicoFixtures.VALOR, response.valor)
        assertEquals(ativo, response.ativo)
    }

    @Test
    @DisplayName("Dado um request, quando converter duas vezes, então deve gerar ids distintos")
    fun givenRequest_whenConvertingTwice_thenGenerateDistinctIds() {
        val request = ServicoFixtures.request()

        assertTrue(mapper.toDomain(request).id != mapper.toDomain(request).id)
    }
}
