package br.com.fiap.oficina.shared.domain

import br.com.fiap.oficina.shared.domain.valueobject.CNPJ
import br.com.fiap.oficina.shared.domain.valueobject.CPF
import br.com.fiap.oficina.shared.domain.valueobject.Document
import br.com.fiap.oficina.shared.domain.valueobject.TipoDocumento
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

@DisplayName("Shared kernel - Document")
class DocumentTest {
    @Test
    @DisplayName("Dada uma entrada com máscara, quando normalizar, então deve manter apenas dígitos")
    fun givenMaskedInput_whenNormalizing_thenKeepOnlyDigits() {
        assertEquals(CPF_VALIDO, Document.normalizar(CPF_FORMATADO))
        assertEquals(CNPJ_VALIDO, Document.normalizar(CNPJ_FORMATADO))
    }

    @Test
    @DisplayName("Dado um cpf com máscara e o tipo, quando criar o documento, então deve normalizar")
    fun givenMaskedCpfAndType_whenCreatingDocument_thenShouldNormalize() {
        val documento = Document.from(CPF_FORMATADO, TipoDocumento.CPF)

        assertEquals(CPF(CPF_VALIDO), documento)
        assertEquals(CPF_VALIDO, documento.valor)
    }

    @Test
    @DisplayName("Dado um cnpj com máscara e o tipo, quando criar o documento, então deve normalizar")
    fun givenMaskedCnpjAndType_whenCreatingDocument_thenShouldNormalize() {
        val documento = Document.from(CNPJ_FORMATADO, TipoDocumento.CNPJ)

        assertEquals(CNPJ(CNPJ_VALIDO), documento)
        assertEquals(CNPJ_VALIDO, documento.valor)
    }

    @Test
    @DisplayName("Dado um documento com 11 dígitos, quando criar sem o tipo, então deve inferir cpf")
    fun givenElevenDigitDocument_whenCreatingWithoutType_thenInferCpf() {
        assertEquals(CPF(CPF_VALIDO), Document.from(CPF_VALIDO))
        assertEquals(CPF(CPF_VALIDO), Document.from(CPF_FORMATADO))
    }

    @Test
    @DisplayName("Dado um documento com 14 dígitos, quando criar sem o tipo, então deve inferir cnpj")
    fun givenFourteenDigitDocument_whenCreatingWithoutType_thenInferCnpj() {
        assertEquals(CNPJ(CNPJ_VALIDO), Document.from(CNPJ_VALIDO))
        assertEquals(CNPJ(CNPJ_VALIDO), Document.from(CNPJ_FORMATADO))
    }

    @Test
    @DisplayName("Dado um documento formatado, quando criar sem o tipo, então deve reconstruir a máscara")
    fun givenFormattedDocument_whenCreatingWithoutType_thenRebuildMask() {
        assertEquals(CPF_FORMATADO, Document.from(CPF_FORMATADO).formatado())
        assertEquals(CNPJ_FORMATADO, Document.from(CNPJ_FORMATADO).formatado())
    }

    @Test
    @DisplayName("Dado um documento com tamanho desconhecido, quando criar sem o tipo, então deve lançar exceção")
    fun givenDocumentWithUnknownLength_whenCreatingWithoutType_thenThrowException() {
        val exception = assertFailsWith<IllegalArgumentException> { Document.from("123") }

        assertEquals("Documento inválido: 123", exception.message)
    }

    @Test
    @DisplayName("Dado um documento vazio, quando criar sem o tipo, então deve lançar exceção")
    fun givenEmptyDocument_whenCreatingWithoutType_thenThrowException() {
        val exception = assertFailsWith<IllegalArgumentException> { Document.from("") }

        assertEquals("Documento inválido: ", exception.message)
    }

    @Test
    @DisplayName(
        "Dado um documento com tamanho de cpf e dígito errado, quando criar sem o tipo, então deve lançar exceção",
    )
    fun givenCpfLengthWithWrongCheckDigit_whenCreatingWithoutType_thenThrowException() {
        val exception = assertFailsWith<IllegalArgumentException> { Document.from("01234567891") }

        assertEquals("CPF inválido: 01234567891", exception.message)
    }

    private companion object {
        const val CPF_VALIDO = "01234567890"
        const val CPF_FORMATADO = "012.345.678-90"
        const val CNPJ_VALIDO = "11222333000181"
        const val CNPJ_FORMATADO = "11.222.333/0001-81"
    }
}
