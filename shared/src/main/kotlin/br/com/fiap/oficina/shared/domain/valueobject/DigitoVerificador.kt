package br.com.fiap.oficina.shared.domain.valueobject

internal object DigitoVerificador {
    private const val MODULO = 11
    private const val RESTO_MINIMO_SEM_DIGITO = 2

    fun validar(valor: String, tamanho: Int, pesosPrimeiroDigito: IntArray, pesosSegundoDigito: IntArray): Boolean {
        if (!formatoValido(valor, tamanho)) return false

        val posicaoPrimeiroDigito = tamanho - 2
        val posicaoSegundoDigito = tamanho - 1

        return digitoConfere(valor, posicaoPrimeiroDigito, pesosPrimeiroDigito) &&
            digitoConfere(valor, posicaoSegundoDigito, pesosSegundoDigito)
    }

    private fun formatoValido(valor: String, tamanho: Int): Boolean =
        valor.length == tamanho && valor.all(Char::isDigit) && !valor.all { it == valor[0] }

    private fun digitoConfere(valor: String, posicao: Int, pesos: IntArray): Boolean =
        calcular(valor.take(posicao), pesos) == valor[posicao].digitToInt()

    private fun calcular(base: String, pesos: IntArray): Int {
        val soma = base.foldIndexed(0) { i, acc, digito -> acc + digito.digitToInt() * pesos[i] }
        val resto = soma % MODULO
        return if (resto < RESTO_MINIMO_SEM_DIGITO) 0 else MODULO - resto
    }
}
