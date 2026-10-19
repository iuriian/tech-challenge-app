package br.com.fiap.oficina.cliente.domain

import br.com.fiap.oficina.shared.domain.valueobject.Document

@ConsistentCopyVisibility
data class Cliente private constructor(
    val id: ClienteId,
    val nome: String,
    val documento: Document,
    val email: String,
    val endereco: Endereco? = null,
    val contatos: List<Contato> = emptyList(),
) {
    init {
        require(nome.isNotBlank()) { "Nome do cliente é obrigatório" }
        require(email.isNotBlank()) { "E-mail do cliente é obrigatório" }
    }

    companion object {
        fun criar(
            nome: String,
            documento: Document,
            email: String,
            endereco: Endereco? = null,
            contatos: List<Contato> = emptyList(),
        ): Cliente =
            Cliente(
                id = ClienteId.generate(),
                nome = nome,
                documento = documento,
                email = email,
                endereco = endereco,
                contatos = contatos,
            )

        fun reconstruir(
            id: String,
            nome: String,
            documento: Document,
            email: String,
            endereco: Endereco? = null,
            contatos: List<Contato> = emptyList(),
        ): Cliente =
            Cliente(
                id = ClienteId.toUUID(id),
                nome = nome,
                documento = documento,
                email = email,
                endereco = endereco,
                contatos = contatos,
            )
    }

    fun atualizar(
        nome: String,
        documento: Document,
        email: String,
        endereco: Endereco? = null,
        contatos: List<Contato> = emptyList(),
    ) = this.copy(
        nome = nome,
        documento = documento,
        email = email,
        endereco = endereco,
        contatos = contatos,
    )
}
