package br.com.fiap.oficina.shared.domain.valueobject

@ConsistentCopyVisibility
data class Placa private constructor(
    val value: String,
) {
    override fun toString(): String = value

    companion object {
        private val PLACA_REGEX = Regex("^[A-Z]{3}[0-9][A-Z0-9][0-9]{2}$")

        fun criar(placa: String): Placa {
            val placaNormalizada = placa.uppercase()

            require(PLACA_REGEX.matches(placaNormalizada)) {
                "Placa inválida: use o formato antigo (ABC1234) ou Mercosul (ABC1D23)"
            }

            return Placa(placaNormalizada)
        }
    }
}
