package br.com.fiap.oficina.veiculo.infrastructure.mapper

import br.com.fiap.oficina.veiculo.VeiculoFixtures
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertFailsWith

@DisplayName("Infraestrutura - Mapper JPA de Veículo")
class VeiculoJPAMapperTest {
    private val mapper = VeiculoJPAMapper()

    @Test
    @DisplayName("Dado uma entidade JPA, quando converter para domínio, então deve preservar os dados")
    fun givenJpaEntity_whenConvertingToDomain_thenPreserveData() {
        val veiculo = mapper.toDomain(VeiculoFixtures.veiculoJPA())

        assertEquals(UUID.fromString(VeiculoFixtures.ID_1), veiculo.id.value)
        assertEquals(VeiculoFixtures.MARCA, veiculo.marca)
        assertEquals(VeiculoFixtures.NOME, veiculo.nome)
        assertEquals(VeiculoFixtures.MODELO, veiculo.modelo)
        assertEquals(VeiculoFixtures.ANO, veiculo.ano)
        assertEquals(VeiculoFixtures.PLACA, veiculo.placa.value)
        assertEquals(UUID.fromString(VeiculoFixtures.PROPRIETARIO_ID), veiculo.proprietarioRef.value)
    }

    @Test
    @DisplayName("Dado um veículo de domínio, quando converter para JPA, então deve preservar os dados")
    fun givenDomainVeiculo_whenConvertingToJpa_thenPreserveData() {
        val jpa = mapper.toJPA(VeiculoFixtures.veiculo())

        assertEquals(UUID.fromString(VeiculoFixtures.ID_1), jpa.id)
        assertEquals(VeiculoFixtures.MARCA, jpa.marca)
        assertEquals(VeiculoFixtures.NOME, jpa.nome)
        assertEquals(VeiculoFixtures.MODELO, jpa.modelo)
        assertEquals(VeiculoFixtures.ANO, jpa.ano)
        assertEquals(VeiculoFixtures.PLACA, jpa.placa)
        assertEquals(UUID.fromString(VeiculoFixtures.PROPRIETARIO_ID), jpa.proprietarioId)
    }

    @Test
    @DisplayName("Dado uma entidade JPA, quando converter ida e volta, então deve preservar a entidade")
    fun givenJpaEntity_whenConvertingRoundTrip_thenPreserveEntity() {
        val jpa = VeiculoFixtures.veiculoJPA(placa = VeiculoFixtures.PLACA_MERCOSUL)

        assertEquals(jpa, mapper.toJPA(mapper.toDomain(jpa)))
    }

    @Test
    @DisplayName("Dado uma entidade JPA com placa inválida, quando converter para domínio, então deve lançar exceção")
    fun givenJpaEntityWithInvalidPlaca_whenConvertingToDomain_thenThrowException() {
        assertFailsWith<IllegalArgumentException> {
            mapper.toDomain(VeiculoFixtures.veiculoJPA(placa = "1234567"))
        }
    }
}
