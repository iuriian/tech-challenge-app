package br.com.fiap.oficina.funcionario.domain

// TODO: Mover para módulo compartilhado
sealed class Document {
    abstract val value: String
}

data class CPF(
    override val value: String,
) : Document()
