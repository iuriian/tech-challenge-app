package br.com.fiap.oficina.shared.domain

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

@DisplayName("Shared kernel - CPF")
class CPFTest {
    @Test
    @DisplayName("Dado um cpf com 11 dígitos, quando criar o cpf, então deve ter sucesso")
    fun givenCpfWithElevenDigits_whenCreatingCpf_thenShouldSucceed() {
        val cpf = CPF("01234567890")

        assertEquals("01234567890", cpf.value)
    }

    @Test
    @DisplayName("Dado um cpf, quando tratado como documento, então deve expor o valor")
    fun givenCpf_whenTreatedAsDocument_thenExposeValue() {
        val documento: Document = CPF("01234567890")

        assertEquals("01234567890", documento.value)
    }

    @Test
    @DisplayName("Dado um cpf vazio, quando criar o cpf, então deve lançar exceção")
    fun givenEmptyCpf_whenCreatingCpf_thenThrowException() {
        val exception =
            assertFailsWith<IllegalArgumentException> {
                CPF("")
            }

        assertEquals("CPF não pode ser vazio", exception.message)
    }

    @Test
    @DisplayName("Dado um cpf em branco, quando criar o cpf, então deve lançar exceção")
    fun givenBlankCpf_whenCreatingCpf_thenThrowException() {
        val exception =
            assertFailsWith<IllegalArgumentException> {
                CPF("           ")
            }

        assertEquals("CPF não pode ser vazio", exception.message)
    }

    @Test
    @DisplayName("Dado um cpf com menos de 11 dígitos, quando criar o cpf, então deve lançar exceção")
    fun givenCpfWithFewerThanElevenDigits_whenCreatingCpf_thenThrowException() {
        val exception =
            assertFailsWith<IllegalArgumentException> {
                CPF("0123456789")
            }

        assertEquals("CPF deve conter 11 dígitos", exception.message)
    }

    @Test
    @DisplayName("Dado um cpf com mais de 11 dígitos, quando criar o cpf, então deve lançar exceção")
    fun givenCpfWithMoreThanElevenDigits_whenCreatingCpf_thenThrowException() {
        val exception =
            assertFailsWith<IllegalArgumentException> {
                CPF("012345678901")
            }

        assertEquals("CPF deve conter 11 dígitos", exception.message)
    }
}
