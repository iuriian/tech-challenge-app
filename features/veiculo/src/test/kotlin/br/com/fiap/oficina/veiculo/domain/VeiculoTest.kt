package br.com.fiap.oficina.veiculo.domain

import br.com.fiap.oficina.shared.domain.valueobject.Placa
import br.com.fiap.oficina.veiculo.VeiculoFixtures
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertFailsWith

@DisplayName("Domínio - Veículo")
class VeiculoTest {
    @Test
    @DisplayName("Dado dados válidos, quando criar veículo, então deve gerar um novo identificador")
    fun givenValidData_whenCreatingVeiculo_thenGenerateNewId() {
        val veiculo =
            Veiculo.criar(
                marca = VeiculoFixtures.MARCA,
                nome = VeiculoFixtures.NOME,
                modelo = VeiculoFixtures.MODELO,
                ano = VeiculoFixtures.ANO,
                placa = VeiculoFixtures.PLACA,
                proprietarioRef = VeiculoFixtures.PROPRIETARIO_ID,
            )

        assertNotNull(veiculo.id.value)
        assertEquals(VeiculoFixtures.MARCA, veiculo.marca)
        assertEquals(VeiculoFixtures.NOME, veiculo.nome)
        assertEquals(VeiculoFixtures.MODELO, veiculo.modelo)
        assertEquals(VeiculoFixtures.ANO, veiculo.ano)
        assertEquals(VeiculoFixtures.PLACA, veiculo.placa.value)
        assertEquals(UUID.fromString(VeiculoFixtures.PROPRIETARIO_ID), veiculo.proprietarioRef.value)
    }

    @Test
    @DisplayName("Dado dois veículos criados, quando comparar os identificadores, então devem ser diferentes")
    fun givenTwoCreatedVeiculos_whenComparingIds_thenTheyAreDifferent() {
        val primeiro = VeiculoFixtures.novoVeiculo()
        val segundo = VeiculoFixtures.novoVeiculo()

        assertNotEquals(primeiro.id, segundo.id)
    }

    @Test
    @DisplayName("Dado uma placa em minúsculas, quando criar veículo, então deve normalizar para maiúsculas")
    fun givenLowercasePlaca_whenCreatingVeiculo_thenNormalizeToUppercase() {
        val veiculo = VeiculoFixtures.novoVeiculo(placa = "abc1d23")

        assertEquals(VeiculoFixtures.PLACA_MERCOSUL, veiculo.placa.value)
    }

    @Test
    @DisplayName("Dado dados persistidos, quando reconstruir veículo, então deve preservar o identificador")
    fun givenPersistedData_whenRebuildingVeiculo_thenPreserveId() {
        val veiculo = VeiculoFixtures.veiculo()

        assertEquals(UUID.fromString(VeiculoFixtures.ID_1), veiculo.id.value)
        assertEquals(VeiculoFixtures.PLACA, veiculo.placa.value)
        assertEquals(UUID.fromString(VeiculoFixtures.PROPRIETARIO_ID), veiculo.proprietarioRef.value)
    }

    @Test
    @DisplayName("Dado uma marca em branco, quando criar veículo, então deve lançar exceção")
    fun givenBlankMarca_whenCreatingVeiculo_thenThrowException() {
        val exception = assertFailsWith<IllegalArgumentException> { VeiculoFixtures.veiculo(marca = "  ") }

        assertEquals("Marca é obrigatória", exception.message)
    }

    @Test
    @DisplayName("Dado um nome em branco, quando criar veículo, então deve lançar exceção")
    fun givenBlankNome_whenCreatingVeiculo_thenThrowException() {
        val exception = assertFailsWith<IllegalArgumentException> { VeiculoFixtures.veiculo(nome = "") }

        assertEquals("Nome do veículo é obrigatório", exception.message)
    }

    @Test
    @DisplayName("Dado um modelo em branco, quando criar veículo, então deve lançar exceção")
    fun givenBlankModelo_whenCreatingVeiculo_thenThrowException() {
        val exception = assertFailsWith<IllegalArgumentException> { VeiculoFixtures.veiculo(modelo = "") }

        assertEquals("Modelo é obrigatório", exception.message)
    }

    @Test
    @DisplayName("Dado um ano em branco, quando criar veículo, então deve lançar exceção")
    fun givenBlankAno_whenCreatingVeiculo_thenThrowException() {
        val exception = assertFailsWith<IllegalArgumentException> { VeiculoFixtures.veiculo(ano = "") }

        assertEquals("Ano é obrigatório", exception.message)
    }

    @Test
    @DisplayName("Dado uma placa inválida, quando criar veículo, então deve lançar exceção")
    fun givenInvalidPlaca_whenCreatingVeiculo_thenThrowException() {
        assertFailsWith<IllegalArgumentException> { VeiculoFixtures.veiculo(placa = "A1C1234") }
        assertFailsWith<IllegalArgumentException> { VeiculoFixtures.novoVeiculo(placa = "ABC123") }
    }

    @Test
    @DisplayName("Dado um proprietário inválido, quando criar veículo, então deve lançar exceção")
    fun givenInvalidProprietario_whenCreatingVeiculo_thenThrowException() {
        assertFailsWith<IllegalArgumentException> { VeiculoFixtures.veiculo(proprietarioRef = "nao-e-um-uuid") }
    }

    @Test
    @DisplayName("Dado um id inválido, quando reconstruir veículo, então deve lançar exceção")
    fun givenInvalidId_whenRebuildingVeiculo_thenThrowException() {
        assertFailsWith<IllegalArgumentException> { VeiculoFixtures.veiculo(id = "nao-e-um-uuid") }
    }

    @Test
    @DisplayName("Dado novos dados, quando atualizar veículo, então deve retornar uma nova instância atualizada")
    fun givenNewData_whenUpdatingVeiculo_thenReturnNewUpdatedInstance() {
        val veiculo = VeiculoFixtures.veiculo()

        val atualizado =
            veiculo.atualizar(
                marca = "Toyota",
                nome = "Corolla",
                modelo = "XEI 2.0",
                ano = "2023",
                placa = Placa.criar(VeiculoFixtures.PLACA_MERCOSUL),
                proprietarioRef = ProprietarioRef.toUUID(VeiculoFixtures.PROPRIETARIO_ID_2),
            )

        assertNotSame(veiculo, atualizado)
        assertEquals(veiculo.id, atualizado.id)
        assertEquals("Toyota", atualizado.marca)
        assertEquals("Corolla", atualizado.nome)
        assertEquals("XEI 2.0", atualizado.modelo)
        assertEquals("2023", atualizado.ano)
        assertEquals(VeiculoFixtures.PLACA_MERCOSUL, atualizado.placa.value)
        assertEquals(UUID.fromString(VeiculoFixtures.PROPRIETARIO_ID_2), atualizado.proprietarioRef.value)
        assertEquals(VeiculoFixtures.MARCA, veiculo.marca)
    }

    @Test
    @DisplayName("Dado dados inválidos, quando atualizar veículo, então deve lançar exceção")
    fun givenInvalidData_whenUpdatingVeiculo_thenThrowException() {
        val veiculo = VeiculoFixtures.veiculo()

        assertFailsWith<IllegalArgumentException> {
            veiculo.atualizar(
                marca = "",
                nome = VeiculoFixtures.NOME,
                modelo = VeiculoFixtures.MODELO,
                ano = VeiculoFixtures.ANO,
                placa = veiculo.placa,
                proprietarioRef = veiculo.proprietarioRef,
            )
        }
    }

    @Test
    @DisplayName("Dado os mesmos dados, quando comparar veículos, então devem ser iguais")
    fun givenSameData_whenComparingVeiculos_thenTheyAreEqual() {
        val primeiro = VeiculoFixtures.veiculo()
        val segundo = VeiculoFixtures.veiculo()

        assertEquals(primeiro, segundo)
        assertEquals(primeiro.hashCode(), segundo.hashCode())
        assertNotEquals(primeiro, VeiculoFixtures.veiculo(id = VeiculoFixtures.ID_2))
    }

    @Test
    @DisplayName("Dado um veículo, quando desestruturar, então deve expor os componentes na ordem declarada")
    fun givenVeiculo_whenDestructuring_thenExposeComponentsInDeclaredOrder() {
        val (id, marca, nome, modelo, ano, placa, proprietarioRef) = VeiculoFixtures.veiculo()

        assertEquals(VeiculoId.toUUID(VeiculoFixtures.ID_1), id)
        assertEquals(VeiculoFixtures.MARCA, marca)
        assertEquals(VeiculoFixtures.NOME, nome)
        assertEquals(VeiculoFixtures.MODELO, modelo)
        assertEquals(VeiculoFixtures.ANO, ano)
        assertEquals(Placa.criar(VeiculoFixtures.PLACA), placa)
        assertEquals(ProprietarioRef.toUUID(VeiculoFixtures.PROPRIETARIO_ID), proprietarioRef)
    }

    @Test
    @DisplayName("Dado um veículo, quando converter para texto, então deve conter os dados principais")
    fun givenVeiculo_whenConvertingToString_thenContainMainData() {
        val texto = VeiculoFixtures.veiculo().toString()

        assertTrue(texto.contains(VeiculoFixtures.MARCA))
        assertTrue(texto.contains(VeiculoFixtures.PLACA))
    }
}
