package br.com.fiap.oficina.funcionario.domain

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.UUID

class FuncionarioTest {
    @Test
    @DisplayName("Dado dados válidos, quando criar funcionário, então deve ter sucesso")
    fun givenValidData_whenCreatingFuncionario_theShouldSucceed() {
        val funcionario =
            Funcionario.criar(
                nome = "João",
                cargo = "Atendente",
                cpf = "01234567890",
            )

        assertEquals("João", funcionario.nome)
        assertEquals(Cargo.ATENDENTE, funcionario.cargo)
        assertNotNull(funcionario.id)
        assertNotNull(funcionario.id.value)
    }

    @Test
    @DisplayName("Dado dois funcionários criados, quando gerar IDs, então deve ter IDs diferentes")
    fun givenTwoFuncionariosCreated_whenGeneratingIds_thenShouldHaveDifferentIds() {
        val f1 =
            Funcionario.criar(
                nome = "A",
                cargo = "Atendente",
                cpf = "01234567890",
            )
        val f2 =
            Funcionario.criar(
                nome = "B",
                cargo = "Atendente",
                cpf = "01234567891",
            )

        assertNotEquals(f1.id, f2.id)
    }

    @Test
    @DisplayName("Dado um ID válido, quando reconstruir funcionário, então deve ter sucesso")
    fun givenValidId_whenReconstructingFuncionario_thenShouldSucceed() {
        val uuid = "00000000-0000-0000-0000-000000000100"
        val funcionario =
            Funcionario.reconstruir(
                id = uuid,
                nome = "Maria",
                cargo = "Mecânico",
                cpf = "01234567890",
            )

        assertEquals(UUID.fromString(uuid), funcionario.id.value)
        assertEquals("Maria", funcionario.nome)
        assertEquals(Cargo.MECANICO, funcionario.cargo)
    }

    @Test
    @DisplayName("Dado um ID inválido, quando reconstruir funcionário, então deve lançar exceção")
    fun givenInvalidId_whenReconstructingFuncionario_thenShouldThrowException() {
        assertThrows(IllegalArgumentException::class.java) {
            Funcionario.reconstruir(id = "invalid-uuid", nome = "X", cargo = "Atendente", cpf = "01234567890")
        }
    }

    @Test
    @DisplayName("Dado um cargo inválido, quando criar funcionário, então deve lançar exceção")
    fun givenInvalidCargo_whenCreatingFuncionario_thenShouldThrowException() {
        assertThrows(IllegalArgumentException::class.java) {
            Funcionario.criar(nome = "Bad", cargo = "UNKNOWN", cpf = "01234567890")
        }
    }

    @Test
    @DisplayName("Dado um cargo inválido, quando reconstruir funcionário, então deve lançar exceção")
    fun givenInvalidCargo_whenReconstructingFuncionario_thenShouldThrowException() {
        val uuid = "00000000-0000-0000-0000-000000000101"
        assertThrows(IllegalArgumentException::class.java) {
            Funcionario.reconstruir(id = uuid, nome = "Bad", cargo = "INVALID", cpf = "01234567890")
        }
    }

    @Test
    @DisplayName("Dado um nome em branco, quando criar funcionário, então deve lançar exceção")
    fun givenBlankNome_whenCreatingFuncionario_thenShouldThrowException() {
        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                Funcionario.criar(nome = "   ", cargo = "Atendente", cpf = "01234567890")
            }

        assertEquals("Nome não pode ser vazio", exception.message)
    }

    @Test
    @DisplayName("Dado um funcionário, quando atualizar os dados, então deve preservar o id")
    fun givenFuncionario_whenUpdatingData_thenShouldPreserveId() {
        val funcionario =
            Funcionario.criar(
                nome = "João",
                cargo = "Atendente",
                cpf = "01234567890",
            )

        val atualizado =
            funcionario.atualizar(
                nome = "João Silva",
                cargo = "Mecânico",
                cpf = "01234567891",
            )

        assertEquals(funcionario.id, atualizado.id)
        assertEquals("João Silva", atualizado.nome)
        assertEquals(Cargo.MECANICO, atualizado.cargo)
        assertEquals("01234567891", atualizado.cpf.value)
    }
}
