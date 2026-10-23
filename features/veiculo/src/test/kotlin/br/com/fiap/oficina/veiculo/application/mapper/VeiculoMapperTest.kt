package br.com.fiap.oficina.veiculo.application.mapper

import br.com.fiap.oficina.veiculo.VeiculoFixtures
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertFailsWith

@DisplayName("Mapper - Veículo")
class VeiculoMapperTest {
    private val mapper = VeiculoMapper()

    @Test
    @DisplayName("Dado um request válido, quando converter para domínio, então deve criar um novo veículo")
    fun givenValidRequest_whenConvertingToDomain_thenCreateNewVeiculo() {
        val veiculo = mapper.toDomain(VeiculoFixtures.veiculoRequest())

        assertNotNull(veiculo.id.value)
        assertEquals(VeiculoFixtures.MARCA, veiculo.marca)
        assertEquals(VeiculoFixtures.NOME, veiculo.nome)
        assertEquals(VeiculoFixtures.MODELO, veiculo.modelo)
        assertEquals(VeiculoFixtures.ANO, veiculo.ano)
        assertEquals(VeiculoFixtures.PLACA, veiculo.placa.value)
        assertEquals(UUID.fromString(VeiculoFixtures.PROPRIETARIO_ID), veiculo.proprietarioRef.value)
    }

    @Test
    @DisplayName("Dado um request com placa inválida, quando converter para domínio, então deve lançar exceção")
    fun givenRequestWithInvalidPlaca_whenConvertingToDomain_thenThrowException() {
        val exception =
            assertFailsWith<IllegalArgumentException> {
                mapper.toDomain(VeiculoFixtures.veiculoRequest(placa = "123ABCD"))
            }

        assertEquals(
            "Placa inválida: use o formato antigo (ABC1234) ou Mercosul (ABC1D23)",
            exception.message,
        )
    }

    @Test
    @DisplayName("Dado um request com proprietário inválido, quando converter para domínio, então deve lançar exceção")
    fun givenRequestWithInvalidProprietario_whenConvertingToDomain_thenThrowException() {
        assertFailsWith<IllegalArgumentException> {
            mapper.toDomain(VeiculoFixtures.veiculoRequest(proprietarioId = "nao-e-um-uuid"))
        }
    }

    @Test
    @DisplayName(
        "Dado um veículo com proprietário, quando converter para response, então deve preencher o proprietário",
    )
    fun givenVeiculoWithProprietario_whenConvertingToResponse_thenFillProprietario() {
        val response = mapper.toResponse(VeiculoFixtures.veiculo(), VeiculoFixtures.dadosProprietario())

        assertEquals(VeiculoFixtures.ID_1, response.id)
        assertEquals(VeiculoFixtures.NOME, response.nome)
        assertEquals(VeiculoFixtures.MARCA, response.marca)
        assertEquals(VeiculoFixtures.MODELO, response.modelo)
        assertEquals(VeiculoFixtures.ANO, response.ano)
        assertEquals(VeiculoFixtures.PLACA, response.placa)
        assertEquals(VeiculoFixtures.PROPRIETARIO_ID, response.proprietario?.id)
        assertEquals(VeiculoFixtures.PROPRIETARIO_NOME, response.proprietario?.nome)
        assertEquals(VeiculoFixtures.PROPRIETARIO_DOCUMENTO, response.proprietario?.numeroDocumento)
    }

    @Test
    @DisplayName("Dado um veículo sem contexto, quando converter para response, então o proprietário deve ser nulo")
    fun givenVeiculoWithoutContext_whenConvertingToResponse_thenProprietarioIsNull() {
        val response = mapper.toResponse(VeiculoFixtures.veiculo(), null)

        assertEquals(VeiculoFixtures.ID_1, response.id)
        assertNull(response.proprietario)
    }

    @Test
    @DisplayName("Dado um request válido, quando converter ida e volta, então deve preservar os dados")
    fun givenValidRequest_whenConvertingRoundTrip_thenPreserveData() {
        val request = VeiculoFixtures.veiculoRequest(placa = "abc1d23")

        val response = mapper.toResponse(mapper.toDomain(request), VeiculoFixtures.dadosProprietario())

        assertEquals(request.nome, response.nome)
        assertEquals(request.marca, response.marca)
        assertEquals(request.modelo, response.modelo)
        assertEquals(request.ano, response.ano)
        assertEquals(VeiculoFixtures.PLACA_MERCOSUL, response.placa)
        assertEquals(request.proprietarioId, response.proprietario?.id)
    }
}
