package br.com.fiap.oficina.servico.infrastructure.provider

import br.com.fiap.oficina.servico.ServicoFixtures
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.math.BigDecimal

@DisplayName("Infraestrutura - Mapper de consulta de Serviço")
class ServicoQueryMapperTest {
    private val mapper = ServicoQueryMapper()

    @Test
    @DisplayName("Dado um serviço de domínio, quando converter, então deve mapear todos os campos")
    fun givenDomainServico_whenConverting_thenMapAllFields() {
        val dto = mapper.toProviderResponse(ServicoFixtures.servico(ativo = true))

        assertEquals(ServicoFixtures.ID_1, dto.id)
        assertEquals(ServicoFixtures.DESCRICAO, dto.descricao)
        assertEquals(ServicoFixtures.VALOR, dto.valor)
        assertTrue(dto.ativo)
    }

    @ParameterizedTest(name = "ativo {0}")
    @ValueSource(booleans = [true, false])
    @DisplayName("Dado um serviço ativo ou inativo, quando converter, então deve preservar a situação")
    fun givenActiveOrInactiveServico_whenConverting_thenPreserveStatus(ativo: Boolean) {
        val dto = mapper.toProviderResponse(ServicoFixtures.servico(ativo = ativo))

        assertEquals(ativo, dto.ativo)
    }

    @Test
    @DisplayName("Dado um id de domínio, quando converter, então deve expor o id como string do UUID")
    fun givenDomainId_whenConverting_thenExposeIdAsUuidString() {
        val servico = ServicoFixtures.servico(id = ServicoFixtures.ID_2)

        val dto = mapper.toProviderResponse(servico)

        assertEquals(servico.id.value.toString(), dto.id)
        assertEquals(ServicoFixtures.ID_2, dto.id)
    }

    @Test
    @DisplayName("Dado um serviço gratuito, quando converter, então deve preservar a escala do valor")
    fun givenFreeServico_whenConverting_thenPreserveValueScale() {
        val valor = BigDecimal("0.00")

        val dto = mapper.toProviderResponse(ServicoFixtures.servico(valor = valor))

        assertEquals(valor, dto.valor)
        assertEquals(valor.scale(), dto.valor.scale())
    }
}
