package br.com.fiap.oficina.shared.domain

import br.com.fiap.oficina.shared.domain.valueobject.CNPJ
import br.com.fiap.oficina.shared.domain.valueobject.CPF
import br.com.fiap.oficina.shared.domain.valueobject.Document
import br.com.fiap.oficina.shared.domain.valueobject.TipoDocumento
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import kotlin.test.assertFailsWith

@DisplayName("Shared kernel - TipoDocumento")
class TipoDocumentoTest {
    @Test
    @DisplayName("Dado o enum, quando listar os valores, então deve conter apenas cpf e cnpj")
    fun givenEnum_whenListingValues_thenContainOnlyCpfAndCnpj() {
        assertEquals(listOf(TipoDocumento.CPF, TipoDocumento.CNPJ), TipoDocumento.entries)
    }

    @ParameterizedTest(name = "tipo {0}")
    @EnumSource(TipoDocumento::class)
    @DisplayName("Dado um tipo, quando converter o nome de volta, então deve retornar o mesmo tipo")
    fun givenType_whenConvertingNameBack_thenReturnSameType(tipo: TipoDocumento) {
        assertEquals(tipo, TipoDocumento.valueOf(tipo.name))
    }

    @Test
    @DisplayName("Dado um nome desconhecido, quando converter, então deve lançar exceção")
    fun givenUnknownName_whenConverting_thenThrowException() {
        assertFailsWith<IllegalArgumentException> { TipoDocumento.valueOf("RG") }
    }

    @Test
    @DisplayName("Dado o tipo cpf, quando criar o documento, então deve produzir um cpf")
    fun givenCpfType_whenCreatingDocument_thenProduceCpf() {
        val documento = Document.from(CPF_VALIDO, TipoDocumento.CPF)

        assertInstanceOf(CPF::class.java, documento)
        assertEquals(TipoDocumento.CPF, documento.tipo)
    }

    @Test
    @DisplayName("Dado o tipo cnpj, quando criar o documento, então deve produzir um cnpj")
    fun givenCnpjType_whenCreatingDocument_thenProduceCnpj() {
        val documento = Document.from(CNPJ_VALIDO, TipoDocumento.CNPJ)

        assertInstanceOf(CNPJ::class.java, documento)
        assertEquals(TipoDocumento.CNPJ, documento.tipo)
    }

    @Test
    @DisplayName("Dado um valor de cnpj com o tipo cpf, quando criar o documento, então deve lançar exceção")
    fun givenCnpjValueWithCpfType_whenCreatingDocument_thenThrowException() {
        val exception = assertFailsWith<IllegalArgumentException> { Document.from(CNPJ_VALIDO, TipoDocumento.CPF) }

        assertEquals("CPF inválido: $CNPJ_VALIDO", exception.message)
    }

    @Test
    @DisplayName("Dado um valor de cpf com o tipo cnpj, quando criar o documento, então deve lançar exceção")
    fun givenCpfValueWithCnpjType_whenCreatingDocument_thenThrowException() {
        val exception = assertFailsWith<IllegalArgumentException> { Document.from(CPF_VALIDO, TipoDocumento.CNPJ) }

        assertEquals("CNPJ inválido: $CPF_VALIDO", exception.message)
    }

    private companion object {
        const val CPF_VALIDO = "01234567890"
        const val CNPJ_VALIDO = "11222333000181"
    }
}
