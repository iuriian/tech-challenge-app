package br.com.fiap.oficina.shared.domain.valueobject

data class CPF(override val valor: String) : Document() {
    override val tipo: TipoDocumento
        get() = TipoDocumento.CPF

    init {
        require(DigitoVerificador.validar(valor, TAMANHO, PESOS_PRIMEIRO_DIGITO, PESOS_SEGUNDO_DIGITO)) {
            "CPF inválido: $valor"
        }
    }

    override fun formatado(): String = valor.replace(MASCARA, "$1.$2.$3-$4")

    companion object {
        internal const val TAMANHO = 11
        private val PESOS_PRIMEIRO_DIGITO = intArrayOf(10, 9, 8, 7, 6, 5, 4, 3, 2)
        private val PESOS_SEGUNDO_DIGITO = intArrayOf(11, 10, 9, 8, 7, 6, 5, 4, 3, 2)
        private val MASCARA = Regex("(\\d{3})(\\d{3})(\\d{3})(\\d{2})")

        fun from(entrada: String): CPF = CPF(normalizar(entrada))
    }
}
