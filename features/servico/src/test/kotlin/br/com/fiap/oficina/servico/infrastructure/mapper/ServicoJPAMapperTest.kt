package br.com.fiap.oficina.servico.infrastructure.mapper

import br.com.fiap.oficina.servico.ServicoFixtures
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.math.BigDecimal
import java.util.UUID
import kotlin.test.assertFailsWith

@DisplayName("Infraestrutura - Mapper JPA de Serviço")
class ServicoJPAMapperTest {
    private val mapper = ServicoJPAMapper()

    @Test
    @DisplayName("Dado um serviço de domínio, quando converter para JPA, então deve mapear todos os campos")
    fun givenDomainServico_whenConvertingToJPA_thenMapAllFields() {
        val jpa = mapper.toJPA(ServicoFixtures.servico(ativo = true))

        assertEquals(UUID.fromString(ServicoFixtures.ID_1), jpa.id)
        assertEquals(ServicoFixtures.DESCRICAO, jpa.descricao)
        assertEquals(ServicoFixtures.VALOR, jpa.valor)
        assertTrue(jpa.ativo)
    }

    @ParameterizedTest(name = "ativo {0}")
    @ValueSource(booleans = [true, false])
    @DisplayName("Dado uma entidade JPA, quando converter para domínio, então deve mapear todos os campos")
    fun givenJPAEntity_whenConvertingToDomain_thenMapAllFields(ativo: Boolean) {
        val servico = mapper.toDomain(ServicoFixtures.servicoJPA(ativo = ativo))

        assertEquals(UUID.fromString(ServicoFixtures.ID_1), servico.id.value)
        assertEquals(ServicoFixtures.DESCRICAO, servico.descricao)
        assertEquals(ServicoFixtures.VALOR, servico.valor)
        assertEquals(ativo, servico.ativo)
    }

    @Test
    @DisplayName("Dado um serviço de domínio, quando converter ida e volta, então deve preservar a igualdade")
    fun givenDomainServico_whenConvertingRoundTrip_thenPreserveEquality() {
        val servico = ServicoFixtures.servico(ativo = true)

        assertEquals(servico, mapper.toDomain(mapper.toJPA(servico)))
    }

    @Test
    @DisplayName("Dado uma entidade JPA, quando converter ida e volta, então deve preservar os campos")
    fun givenJPAEntity_whenConvertingRoundTrip_thenPreserveFields() {
        val jpa = ServicoFixtures.servicoJPA(ativo = false)

        val resultado = mapper.toJPA(mapper.toDomain(jpa))

        assertEquals(jpa.id, resultado.id)
        assertEquals(jpa.descricao, resultado.descricao)
        assertEquals(jpa.valor, resultado.valor)
        assertFalse(resultado.ativo)
    }

    @Test
    @DisplayName("Dado uma entidade JPA com descrição em branco, quando converter, então deve lançar exceção")
    fun givenJPAEntityWithBlankDescription_whenConverting_thenThrowException() {
        val exception =
            assertFailsWith<IllegalArgumentException> {
                mapper.toDomain(ServicoFixtures.servicoJPA(descricao = " "))
            }

        assertEquals("Descrição do serviço é obrigatória", exception.message)
    }

    @Test
    @DisplayName("Dado uma entidade JPA com valor negativo, quando converter, então deve lançar exceção")
    fun givenJPAEntityWithNegativeValue_whenConverting_thenThrowException() {
        val exception =
            assertFailsWith<IllegalArgumentException> {
                mapper.toDomain(ServicoFixtures.servicoJPA(valor = BigDecimal("-1.00")))
            }

        assertEquals("Valor do serviço não pode ser negativo", exception.message)
    }
}
