package br.com.fiap.oficina.veiculo.infrastructure.acl

import br.com.fiap.oficina.veiculo.VeiculoFixtures
import br.com.fiap.oficina.veiculo.domain.ProprietarioRef
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

@DisplayName("Infraestrutura - Mapper de proprietário")
class ProprietarioMapperTest {
    private val mapper = ProprietarioMapper()

    @Test
    @DisplayName("Dado um dto de cliente, quando converter, então deve retornar os dados do proprietário")
    fun givenClienteDto_whenConverting_thenReturnProprietarioData() {
        val dados = mapper.toProviderResponse(VeiculoFixtures.clienteQueryDto())

        assertEquals(ProprietarioRef.toUUID(VeiculoFixtures.PROPRIETARIO_ID), dados.ref)
        assertEquals(VeiculoFixtures.PROPRIETARIO_NOME, dados.nome)
        assertEquals(VeiculoFixtures.PROPRIETARIO_DOCUMENTO, dados.numeroDocumento)
    }

    @Test
    @DisplayName("Dado um dto com id inválido, quando converter, então deve lançar exceção")
    fun givenDtoWithInvalidId_whenConverting_thenThrowException() {
        assertFailsWith<IllegalArgumentException> {
            mapper.toProviderResponse(VeiculoFixtures.clienteQueryDto(id = "nao-e-um-uuid"))
        }
    }

    @Test
    @DisplayName("Dado os mesmos dados, quando comparar os proprietários, então devem ser iguais")
    fun givenSameData_whenComparingProprietarios_thenTheyAreEqual() {
        val primeiro = mapper.toProviderResponse(VeiculoFixtures.clienteQueryDto())
        val segundo = mapper.toProviderResponse(VeiculoFixtures.clienteQueryDto())

        assertEquals(primeiro, segundo)
        assertEquals(primeiro.hashCode(), segundo.hashCode())
        assertEquals(primeiro, primeiro.copy())
        assertEquals(VeiculoFixtures.PROPRIETARIO_NOME, primeiro.component2())
        assertEquals(true, primeiro.toString().contains(VeiculoFixtures.PROPRIETARIO_NOME))
    }
}
