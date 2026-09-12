package br.com.fiap.oficina.funcionario.domain

enum class Cargo(
    val id: Int,
    val descricao: String,
) {
    ATENDENTE(1, "Atendente"),
    MECANICO(2, "Mecânico"),
    ;

    companion object {
        fun fromId(id: Int): Cargo =
            entries.firstOrNull { it.id == id } ?: throw IllegalArgumentException("Cargo inválido!")

        fun fromName(name: String?): Cargo {
            require(!name.isNullOrBlank()) { "Cargo não pode ser nulo ou vazio" }

            return entries.firstOrNull { it.name == name } ?: throw IllegalArgumentException("Cargo inválido!")
        }
    }
}
