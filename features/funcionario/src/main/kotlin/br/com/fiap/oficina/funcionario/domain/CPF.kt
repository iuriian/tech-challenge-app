package br.com.fiap.oficina.funcionario.domain

sealed class Document {
    abstract val value: String
}

data class CPF(
    override val value: String,
) : Document() {
    private val minLength = 11

    init {
        require(value.isNotBlank()) { "CPF não pode ser vazio" }
        require(value.length == minLength) { "CPF deve conter $minLength dígitos" }
    }
}
