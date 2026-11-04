package br.com.fiap.oficina.ordemservico.domain

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.assertFailsWith

@DisplayName("Domínio - Número da Ordem de Serviço")
class NumeroOrdemServicoTest {
    @Test
    @DisplayName("Dado um sequencial e um ano, quando criar o número, então deve formatar com zeros à esquerda")
    fun givenSequentialAndYear_whenCreatingNumber_thenPadWithLeadingZeros() {
        assertEquals("OS-2026-000042", NumeroOrdemServico.criar(sequencial = 42, ano = 2026).valor)
    }

    @Test
    @DisplayName("Dado um sequencial longo, quando criar o número, então deve manter os dígitos")
    fun givenLongSequential_whenCreatingNumber_thenKeepDigits() {
        assertEquals("OS-2026-1234567", NumeroOrdemServico.criar(sequencial = 1234567, ano = 2026).valor)
    }

    @Test
    @DisplayName("Dado um número, quando convertido para texto, então deve retornar o valor")
    fun givenNumber_whenConvertedToText_thenReturnValue() {
        assertEquals("OS-2026-000001", NumeroOrdemServico.criar(sequencial = 1, ano = 2026).toString())
    }

    @ParameterizedTest(name = "sequencial {0}")
    @ValueSource(longs = [0, -1])
    @DisplayName("Dado um sequencial não positivo, quando criar o número, então deve lançar exceção")
    fun givenNonPositiveSequential_whenCreatingNumber_thenThrowException(sequencial: Long) {
        val exception =
            assertFailsWith<IllegalArgumentException> {
                NumeroOrdemServico.criar(sequencial = sequencial, ano = 2026)
            }

        assertEquals("Sequencial da ordem de serviço deve ser positivo", exception.message)
    }

    @ParameterizedTest(name = "valor [{0}]")
    @ValueSource(strings = ["", " "])
    @DisplayName("Dado um valor em branco, quando instanciar o número, então deve lançar exceção")
    fun givenBlankValue_whenInstantiatingNumber_thenThrowException(valor: String) {
        val exception = assertFailsWith<IllegalArgumentException> { NumeroOrdemServico(valor) }

        assertEquals("Número da ordem de serviço é obrigatório", exception.message)
    }

    @Test
    @DisplayName("Dado um valor acima do tamanho máximo, quando instanciar o número, então deve lançar exceção")
    fun givenValueAboveMaxLength_whenInstantiatingNumber_thenThrowException() {
        val exception = assertFailsWith<IllegalArgumentException> { NumeroOrdemServico("O".repeat(51)) }

        assertEquals("Número da ordem de serviço deve possuir no máximo 50 caracteres", exception.message)
    }

    @Test
    @DisplayName("Dado o tamanho máximo exato, quando instanciar o número, então deve ser aceito")
    fun givenExactMaxLength_whenInstantiatingNumber_thenAccept() {
        assertEquals(50, NumeroOrdemServico("O".repeat(50)).valor.length)
    }

    @Test
    @DisplayName("Dado o mesmo valor, quando comparar os números, então devem ser iguais")
    fun givenSameValue_whenComparingNumbers_thenTheyAreEqual() {
        val primeiro = NumeroOrdemServico.criar(sequencial = 7, ano = 2026)
        val segundo = NumeroOrdemServico.criar(sequencial = 7, ano = 2026)

        assertEquals(primeiro, segundo)
        assertEquals(primeiro.hashCode(), segundo.hashCode())
        assertEquals(primeiro, primeiro.copy())
        assertEquals("OS-2026-000007", primeiro.component1())
    }
}
