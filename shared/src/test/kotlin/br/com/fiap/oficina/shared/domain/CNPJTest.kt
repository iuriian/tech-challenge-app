package br.com.fiap.oficina.shared.domain

import br.com.fiap.oficina.shared.domain.valueobject.CNPJ
import br.com.fiap.oficina.shared.domain.valueobject.Document
import br.com.fiap.oficina.shared.domain.valueobject.TipoDocumento
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

@DisplayName("Shared kernel - CNPJ")
class CNPJTest {
    @Test
    @DisplayName("Dado um cnpj com 14 dígitos válidos, quando criar o cnpj, então deve ter sucesso")
    fun givenCnpjWithFourteenValidDigits_whenCreatingCnpj_thenShouldSucceed() {
        val cnpj = CNPJ(VALIDO)

        assertEquals(VALIDO, cnpj.valor)
        assertEquals(TipoDocumento.CNPJ, cnpj.tipo)
    }

    @Test
    @DisplayName("Dado um cnpj, quando tratado como documento, então deve expor o valor")
    fun givenCnpj_whenTreatedAsDocument_thenExposeValue() {
        val documento: Document = CNPJ(VALIDO)

        assertEquals(VALIDO, documento.valor)
    }

    @Test
    @DisplayName("Dado um cnpj com máscara, quando criar pela factory, então deve normalizar")
    fun givenMaskedCnpj_whenCreatingFromFactory_thenShouldNormalize() {
        assertEquals(VALIDO, CNPJ.from(VALIDO_FORMATADO).valor)
    }

    @Test
    @DisplayName("Dado um cnpj válido, quando formatar, então deve aplicar a máscara")
    fun givenValidCnpj_whenFormatting_thenApplyMask() {
        assertEquals(VALIDO_FORMATADO, CNPJ(VALIDO).formatado())
    }

    @Test
    @DisplayName("Dado um cnpj válido, quando converter para texto, então deve expor tipo e máscara")
    fun givenValidCnpj_whenConvertingToString_thenExposeTypeAndMask() {
        assertEquals("CNPJ($VALIDO_FORMATADO)", CNPJ(VALIDO).toString())
    }

    @Test
    @DisplayName("Dado um cnpj vazio, quando criar o cnpj, então deve lançar exceção")
    fun givenEmptyCnpj_whenCreatingCnpj_thenThrowException() {
        assertMensagemDeCnpjInvalido("")
    }

    @Test
    @DisplayName("Dado um cnpj com menos de 14 dígitos, quando criar o cnpj, então deve lançar exceção")
    fun givenCnpjWithFewerThanFourteenDigits_whenCreatingCnpj_thenThrowException() {
        assertMensagemDeCnpjInvalido("1122233300018")
    }

    @Test
    @DisplayName("Dado um cnpj com mais de 14 dígitos, quando criar o cnpj, então deve lançar exceção")
    fun givenCnpjWithMoreThanFourteenDigits_whenCreatingCnpj_thenThrowException() {
        assertMensagemDeCnpjInvalido("112223330001811")
    }

    @Test
    @DisplayName("Dado um cnpj com máscara, quando criar pelo construtor, então deve lançar exceção")
    fun givenMaskedCnpj_whenCreatingFromConstructor_thenThrowException() {
        assertMensagemDeCnpjInvalido(VALIDO_FORMATADO)
    }

    @Test
    @DisplayName("Dado um cnpj com todos os dígitos iguais, quando criar o cnpj, então deve lançar exceção")
    fun givenCnpjWithRepeatedDigits_whenCreatingCnpj_thenThrowException() {
        assertMensagemDeCnpjInvalido("11111111111111")
    }

    @Test
    @DisplayName("Dado um cnpj com dígito verificador errado, quando criar o cnpj, então deve lançar exceção")
    fun givenCnpjWithWrongCheckDigit_whenCreatingCnpj_thenThrowException() {
        assertMensagemDeCnpjInvalido("11222333000100")
    }

    private fun assertMensagemDeCnpjInvalido(entrada: String) {
        val exception = assertFailsWith<IllegalArgumentException> { CNPJ(entrada) }

        assertEquals("CNPJ inválido: $entrada", exception.message)
    }

    private companion object {
        const val VALIDO = "11222333000181"
        const val VALIDO_FORMATADO = "11.222.333/0001-81"
    }
}
