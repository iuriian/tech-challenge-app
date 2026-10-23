package br.com.fiap.oficina.shared.domain.valueobject

data class CNPJ(override val valor: String) : Document() {
    override val tipo: TipoDocumento
        get() = TipoDocumento.CNPJ

    init {
        require(DigitoVerificador.validar(valor, TAMANHO, PESOS_PRIMEIRO_DIGITO, PESOS_SEGUNDO_DIGITO)) {
            "CNPJ inválido: $valor"
        }
    }

    override fun formatado(): String = valor.replace(MASCARA, "$1.$2.$3/$4-$5")

    companion object {
        internal const val TAMANHO = 14
        private val PESOS_PRIMEIRO_DIGITO = intArrayOf(5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2)
        private val PESOS_SEGUNDO_DIGITO = intArrayOf(6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2)
        private val MASCARA = Regex("(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})")

        fun from(entrada: String): CNPJ = CNPJ(normalizar(entrada))
    }
}
