package br.com.fiap.oficina.funcionario.domain

// TODO: Mover para módulo compartilhado
sealed class Document {
    abstract val value: String
}

data class CPF(
    override val value: String,
) : Document() {
    init {
        require(value.isNotBlank()) { "CPF não pode ser vazio" }
        require(value.length == 11) { "CPF deve conter 11 dígitos" }
    }
}
