package br.com.fiap.oficina.shared.domain

import br.com.fiap.oficina.shared.domain.valueobject.DigitoVerificador
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

@DisplayName("Shared kernel - DigitoVerificador")
class DigitoVerificadorTest {
    @Nested
    @DisplayName("Com os pesos de CPF")
    inner class ComPesosDeCpf {
        @ParameterizedTest(name = "cpf {0}")
        @ValueSource(strings = [CPF_VALIDO, "10000000108", "10000000280", "52998224725"])
        @DisplayName("Dado um valor com dígitos verificadores corretos, quando validar, então deve aceitar")
        fun givenValueWithCorrectCheckDigits_whenValidating_thenAccept(valor: String) {
            assertTrue(validarCpf(valor))
        }

        @ParameterizedTest(name = "cpf {0}")
        @ValueSource(strings = ["0123456780", "012345678901", "", "0"])
        @DisplayName("Dado um valor com tamanho diferente do esperado, quando validar, então deve recusar")
        fun givenValueWithUnexpectedLength_whenValidating_thenReject(valor: String) {
            assertFalse(validarCpf(valor))
        }

        @ParameterizedTest(name = "cpf {0}")
        @ValueSource(strings = ["0123456789a", "012.4567890", "0123456789 ", "-1234567890"])
        @DisplayName("Dado um valor com caracteres não numéricos, quando validar, então deve recusar")
        fun givenValueWithNonDigits_whenValidating_thenReject(valor: String) {
            assertFalse(validarCpf(valor))
        }

        @ParameterizedTest(name = "cpf {0}")
        @ValueSource(strings = ["00000000000", "11111111111", "99999999999"])
        @DisplayName("Dado um valor com todos os dígitos iguais, quando validar, então deve recusar")
        fun givenValueWithRepeatedDigits_whenValidating_thenReject(valor: String) {
            assertFalse(validarCpf(valor))
        }

        @Test
        @DisplayName("Dado um valor com o primeiro dígito verificador errado, quando validar, então deve recusar")
        fun givenValueWithWrongFirstCheckDigit_whenValidating_thenReject() {
            assertFalse(validarCpf("01234567800"))
        }

        @Test
        @DisplayName("Dado um valor com o segundo dígito verificador errado, quando validar, então deve recusar")
        fun givenValueWithWrongSecondCheckDigit_whenValidating_thenReject() {
            assertFalse(validarCpf("01234567891"))
        }

        @Test
        @DisplayName("Dado um valor válido com os dígitos invertidos, quando validar, então deve recusar")
        fun givenValidValueWithSwappedCheckDigits_whenValidating_thenReject() {
            assertFalse(validarCpf("01234567809"))
        }
    }

    @Nested
    @DisplayName("Com os pesos de CNPJ")
    inner class ComPesosDeCnpj {
        @ParameterizedTest(name = "cnpj {0}")
        @ValueSource(strings = [CNPJ_VALIDO, "11222333000505", "11222333001820", "04252011000110"])
        @DisplayName("Dado um valor com dígitos verificadores corretos, quando validar, então deve aceitar")
        fun givenValueWithCorrectCheckDigits_whenValidating_thenAccept(valor: String) {
            assertTrue(validarCnpj(valor))
        }

        @ParameterizedTest(name = "cnpj {0}")
        @ValueSource(strings = ["1122233300018", "112223330001811", "", "1"])
        @DisplayName("Dado um valor com tamanho diferente do esperado, quando validar, então deve recusar")
        fun givenValueWithUnexpectedLength_whenValidating_thenReject(valor: String) {
            assertFalse(validarCnpj(valor))
        }

        @ParameterizedTest(name = "cnpj {0}")
        @ValueSource(strings = ["1122233300018X", "11.22233300018", "1122233300018 "])
        @DisplayName("Dado um valor com caracteres não numéricos, quando validar, então deve recusar")
        fun givenValueWithNonDigits_whenValidating_thenReject(valor: String) {
            assertFalse(validarCnpj(valor))
        }

        @ParameterizedTest(name = "cnpj {0}")
        @ValueSource(strings = ["00000000000000", "11111111111111", "99999999999999"])
        @DisplayName("Dado um valor com todos os dígitos iguais, quando validar, então deve recusar")
        fun givenValueWithRepeatedDigits_whenValidating_thenReject(valor: String) {
            assertFalse(validarCnpj(valor))
        }

        @Test
        @DisplayName("Dado um valor com o primeiro dígito verificador errado, quando validar, então deve recusar")
        fun givenValueWithWrongFirstCheckDigit_whenValidating_thenReject() {
            assertFalse(validarCnpj("11222333000191"))
        }

        @Test
        @DisplayName("Dado um valor com o segundo dígito verificador errado, quando validar, então deve recusar")
        fun givenValueWithWrongSecondCheckDigit_whenValidating_thenReject() {
            assertFalse(validarCnpj("11222333000182"))
        }

        @Test
        @DisplayName("Dado um valor válido com os dígitos invertidos, quando validar, então deve recusar")
        fun givenValidValueWithSwappedCheckDigits_whenValidating_thenReject() {
            assertFalse(validarCnpj("11222333000118"))
        }
    }

    @Nested
    @DisplayName("Com pesos trocados entre os documentos")
    inner class ComPesosTrocados {
        @Test
        @DisplayName("Dado um cpf válido, quando validar com os pesos de cnpj, então deve recusar pelo tamanho")
        fun givenValidCpf_whenValidatingWithCnpjWeights_thenRejectByLength() {
            assertFalse(validarCnpj(CPF_VALIDO))
        }

        @Test
        @DisplayName("Dado um cnpj válido, quando validar com os pesos de cpf, então deve recusar pelo tamanho")
        fun givenValidCnpj_whenValidatingWithCpfWeights_thenRejectByLength() {
            assertFalse(validarCpf(CNPJ_VALIDO))
        }
    }

    private fun validarCpf(valor: String): Boolean =
        DigitoVerificador.validar(valor, TAMANHO_CPF, PESOS_CPF_PRIMEIRO_DIGITO, PESOS_CPF_SEGUNDO_DIGITO)

    private fun validarCnpj(valor: String): Boolean =
        DigitoVerificador.validar(valor, TAMANHO_CNPJ, PESOS_CNPJ_PRIMEIRO_DIGITO, PESOS_CNPJ_SEGUNDO_DIGITO)

    private companion object {
        const val CPF_VALIDO = "01234567890"
        const val CNPJ_VALIDO = "11222333000181"

        const val TAMANHO_CPF = 11
        const val TAMANHO_CNPJ = 14

        val PESOS_CPF_PRIMEIRO_DIGITO = intArrayOf(10, 9, 8, 7, 6, 5, 4, 3, 2)
        val PESOS_CPF_SEGUNDO_DIGITO = intArrayOf(11, 10, 9, 8, 7, 6, 5, 4, 3, 2)
        val PESOS_CNPJ_PRIMEIRO_DIGITO = intArrayOf(5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2)
        val PESOS_CNPJ_SEGUNDO_DIGITO = intArrayOf(6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2)
    }
}
