package br.com.fiap.oficina.shared.domain.valueobject

abstract class Document {
    abstract val valor: String
    abstract val tipo: TipoDocumento

    abstract fun formatado(): String

    final override fun toString(): String = "${tipo.name}(${formatado()})"

    companion object {
        private val NAO_DIGITOS = Regex("\\D")

        fun normalizar(entrada: String): String = entrada.replace(NAO_DIGITOS, "")

        fun from(valor: String, tipo: TipoDocumento): Document = when (tipo) {
            TipoDocumento.CPF -> CPF.from(valor)
            TipoDocumento.CNPJ -> CNPJ.from(valor)
        }

        fun from(entrada: String): Document {
            val normalizado = normalizar(entrada)

            return when (normalizado.length) {
                CPF.TAMANHO -> CPF(normalizado)
                CNPJ.TAMANHO -> CNPJ(normalizado)
                else -> throw IllegalArgumentException("Documento inválido: $entrada")
            }
        }
    }
}

enum class TipoDocumento {
    CPF,
    CNPJ,
}
