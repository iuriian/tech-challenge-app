package br.com.fiap.oficina.funcionario.domain

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

@DisplayName("Value object - Cargo")
class CargoTest {
    @Test
    @DisplayName("Dado um id existente, quando buscar cargo por id, então deve retornar o cargo")
    fun givenExistingId_whenFindingCargoById_thenReturnCargo() {
        assertEquals(Cargo.ATENDENTE, Cargo.fromId(1))
        assertEquals(Cargo.MECANICO, Cargo.fromId(2))
    }

    @Test
    @DisplayName("Dado um id inexistente, quando buscar cargo por id, então deve lançar exceção")
    fun givenNonExistentId_whenFindingCargoById_thenThrowException() {
        val exception =
            assertFailsWith<IllegalArgumentException> {
                Cargo.fromId(99)
            }

        assertEquals("Cargo inválido!", exception.message)
    }

    @Test
    @DisplayName("Dado um nome existente, quando buscar cargo por nome, então deve retornar o cargo")
    fun givenExistingName_whenFindingCargoByName_thenReturnCargo() {
        assertEquals(Cargo.ATENDENTE, Cargo.fromName("ATENDENTE"))
        assertEquals(Cargo.MECANICO, Cargo.fromName("MECANICO"))
    }

    @Test
    @DisplayName("Dado um nome nulo, quando buscar cargo por nome, então deve lançar exceção")
    fun givenNullName_whenFindingCargoByName_thenThrowException() {
        val exception =
            assertFailsWith<IllegalArgumentException> {
                Cargo.fromName(null)
            }

        assertEquals("Cargo não pode ser nulo ou vazio", exception.message)
    }

    @Test
    @DisplayName("Dado um nome em branco, quando buscar cargo por nome, então deve lançar exceção")
    fun givenBlankName_whenFindingCargoByName_thenThrowException() {
        val exception =
            assertFailsWith<IllegalArgumentException> {
                Cargo.fromName("   ")
            }

        assertEquals("Cargo não pode ser nulo ou vazio", exception.message)
    }

    @Test
    @DisplayName("Dado um nome inexistente, quando buscar cargo por nome, então deve lançar exceção")
    fun givenNonExistentName_whenFindingCargoByName_thenThrowException() {
        val exception =
            assertFailsWith<IllegalArgumentException> {
                Cargo.fromName("GERENTE")
            }

        assertEquals("Cargo inválido!", exception.message)
    }

    @Test
    @DisplayName("Dado um cargo, quando consultar id e descrição, então deve retornar os valores do catálogo")
    fun givenCargo_whenReadingIdAndDescricao_thenReturnCatalogValues() {
        assertEquals(1, Cargo.ATENDENTE.id)
        assertEquals("Atendente", Cargo.ATENDENTE.descricao)
        assertEquals(2, Cargo.MECANICO.id)
        assertEquals("Mecânico", Cargo.MECANICO.descricao)
    }
}
