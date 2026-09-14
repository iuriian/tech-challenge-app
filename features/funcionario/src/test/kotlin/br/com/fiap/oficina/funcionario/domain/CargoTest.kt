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
    @DisplayName("Dado uma descrição existente, quando buscar cargo por descrição, então deve retornar o cargo")
    fun givenExistingDescricao_whenFindingCargoByDescricao_thenReturnCargo() {
        assertEquals(Cargo.ATENDENTE, Cargo.fromDescricao("Atendente"))
        assertEquals(Cargo.MECANICO, Cargo.fromDescricao("Mecânico"))
    }

    @Test
    @DisplayName("Dado uma descrição nula, quando buscar cargo por descrição, então deve lançar exceção")
    fun givenNullDescricao_whenFindingCargoByDescricao_thenThrowException() {
        val exception =
            assertFailsWith<IllegalArgumentException> {
                Cargo.fromDescricao(null)
            }

        assertEquals("Cargo não pode ser nulo ou vazio", exception.message)
    }

    @Test
    @DisplayName("Dado uma descrição em branco, quando buscar cargo por descrição, então deve lançar exceção")
    fun givenBlankDescricao_whenFindingCargoByDescricao_thenThrowException() {
        val exception =
            assertFailsWith<IllegalArgumentException> {
                Cargo.fromDescricao("   ")
            }

        assertEquals("Cargo não pode ser nulo ou vazio", exception.message)
    }

    @Test
    @DisplayName("Dado uma descrição inexistente, quando buscar cargo por descrição, então deve lançar exceção")
    fun givenNonExistentDescricao_whenFindingCargoByDescricao_thenThrowException() {
        val exception =
            assertFailsWith<IllegalArgumentException> {
                Cargo.fromDescricao("GERENTE")
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
