package br.com.fiap.oficina.shared.domain

import br.com.fiap.oficina.shared.domain.valueobject.CPF
import br.com.fiap.oficina.shared.domain.valueobject.Document
import br.com.fiap.oficina.shared.domain.valueobject.TipoDocumento
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

@DisplayName("Shared kernel - CPF")
class CPFTest {
    @Test
    @DisplayName("Dado um cpf com 11 dígitos válidos, quando criar o cpf, então deve ter sucesso")
    fun givenCpfWithElevenValidDigits_whenCreatingCpf_thenShouldSucceed() {
        val cpf = CPF(VALIDO)

        assertEquals(VALIDO, cpf.valor)
        assertEquals(TipoDocumento.CPF, cpf.tipo)
    }

    @Test
    @DisplayName("Dado um cpf, quando tratado como documento, então deve expor o valor")
    fun givenCpf_whenTreatedAsDocument_thenExposeValue() {
        val documento: Document = CPF(VALIDO)

        assertEquals(VALIDO, documento.valor)
    }

    @Test
    @DisplayName("Dado um cpf com máscara, quando criar pela factory, então deve normalizar")
    fun givenMaskedCpf_whenCreatingFromFactory_thenShouldNormalize() {
        assertEquals(VALIDO, CPF.from(VALIDO_FORMATADO).valor)
    }

    @Test
    @DisplayName("Dado um cpf válido, quando formatar, então deve aplicar a máscara")
    fun givenValidCpf_whenFormatting_thenApplyMask() {
        assertEquals(VALIDO_FORMATADO, CPF(VALIDO).formatado())
    }

    @Test
    @DisplayName("Dado um cpf válido, quando converter para texto, então deve expor tipo e máscara")
    fun givenValidCpf_whenConvertingToString_thenExposeTypeAndMask() {
        assertEquals("CPF($VALIDO_FORMATADO)", CPF(VALIDO).toString())
    }

    @Test
    @DisplayName("Dado um cpf vazio, quando criar o cpf, então deve lançar exceção")
    fun givenEmptyCpf_whenCreatingCpf_thenThrowException() {
        assertMensagemDeCpfInvalido("")
    }

    @Test
    @DisplayName("Dado um cpf em branco, quando criar o cpf, então deve lançar exceção")
    fun givenBlankCpf_whenCreatingCpf_thenThrowException() {
        assertMensagemDeCpfInvalido("           ")
    }

    @Test
    @DisplayName("Dado um cpf com menos de 11 dígitos, quando criar o cpf, então deve lançar exceção")
    fun givenCpfWithFewerThanElevenDigits_whenCreatingCpf_thenThrowException() {
        assertMensagemDeCpfInvalido("0123456789")
    }

    @Test
    @DisplayName("Dado um cpf com mais de 11 dígitos, quando criar o cpf, então deve lançar exceção")
    fun givenCpfWithMoreThanElevenDigits_whenCreatingCpf_thenThrowException() {
        assertMensagemDeCpfInvalido("012345678901")
    }

    @Test
    @DisplayName("Dado um cpf com máscara, quando criar pelo construtor, então deve lançar exceção")
    fun givenMaskedCpf_whenCreatingFromConstructor_thenThrowException() {
        assertMensagemDeCpfInvalido(VALIDO_FORMATADO)
    }

    @Test
    @DisplayName("Dado um cpf com todos os dígitos iguais, quando criar o cpf, então deve lançar exceção")
    fun givenCpfWithRepeatedDigits_whenCreatingCpf_thenThrowException() {
        assertMensagemDeCpfInvalido("11111111111")
    }

    @Test
    @DisplayName("Dado um cpf com primeiro dígito verificador errado, quando criar o cpf, então deve lançar exceção")
    fun givenCpfWithWrongFirstCheckDigit_whenCreatingCpf_thenThrowException() {
        assertMensagemDeCpfInvalido("01234567800")
    }

    @Test
    @DisplayName("Dado um cpf com segundo dígito verificador errado, quando criar o cpf, então deve lançar exceção")
    fun givenCpfWithWrongSecondCheckDigit_whenCreatingCpf_thenThrowException() {
        assertMensagemDeCpfInvalido("01234567891")
    }

    private fun assertMensagemDeCpfInvalido(entrada: String) {
        val exception = assertFailsWith<IllegalArgumentException> { CPF(entrada) }

        assertEquals("CPF inválido: $entrada", exception.message)
    }

    private companion object {
        const val VALIDO = "01234567890"
        const val VALIDO_FORMATADO = "012.345.678-90"
    }
}
