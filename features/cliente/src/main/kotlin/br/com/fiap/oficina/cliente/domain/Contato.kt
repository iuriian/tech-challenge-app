package br.com.fiap.oficina.cliente.domain

@ConsistentCopyVisibility
data class Contato private constructor(
    val id: ContatoId,
    val tipo: String,
    val nome: String,
    val telefone: String,
) {
    companion object {
        fun criar(
            tipo: String,
            nome: String,
            telefone: String,
        ): Contato {
            require(tipo.isNotBlank()) { "Tipo do contato é obrigatório" }
            require(nome.isNotBlank()) { "Nome do contato é obrigatório" }
            require(telefone.isNotBlank()) { "Telefone do contato é obrigatório" }

            return Contato(
                id = ContatoId.generate(),
                tipo = tipo,
                nome = nome,
                telefone = telefone,
            )
        }

        fun reconstruir(
            id: String,
            tipo: String,
            nome: String,
            telefone: String,
        ): Contato =
            Contato(
                id = ContatoId.toUUID(id),
                tipo = tipo,
                nome = nome,
                telefone = telefone,
            )
    }
}
