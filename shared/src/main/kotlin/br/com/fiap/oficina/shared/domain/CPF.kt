package br.com.fiap.oficina.shared.domain

data class CPF(override val value: String) : Document() {
    init {
        require(value.isNotBlank()) { "CPF não pode ser vazio" }
        require(value.length == TAMANHO) { "CPF deve conter $TAMANHO dígitos" }
    }

    private companion object {
        const val TAMANHO = 11
    }
}
