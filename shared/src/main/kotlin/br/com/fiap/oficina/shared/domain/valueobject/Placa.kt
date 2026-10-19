package br.com.fiap.oficina.shared.domain.valueobject

data class Placa private constructor(
    val value: String,
) {
    companion object {
        private val PLACA_REGEX = Regex("^[A-Za-z]{3}[0-9][A-Za-z0-9][0-9]{2}$")

        fun criar(placa: String): Placa {
            require(PLACA_REGEX.matches(placa)) {
                "Placa inválida: use o formato antigo (ABC1234) ou Mercosul (ABC1D23)"
            }
            return Placa(placa)
        }
    }
}
