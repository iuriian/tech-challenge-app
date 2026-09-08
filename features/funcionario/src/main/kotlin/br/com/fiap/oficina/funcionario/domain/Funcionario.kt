package br.com.fiap.oficina.funcionario.domain

internal class Funcionario(
    val id: FuncionarioId,
    val nome: String,
    val cpf: CPF,
    val cargo: Cargo,
) {
    companion object {
        fun criar(
            nome: String,
            cpf: CPF,
            cargo: String,
        ): Funcionario =
            Funcionario(
                id = FuncionarioId.generate(),
                nome = nome,
                cpf = cpf,
                cargo = Cargo.fromName(cargo),
            )

        fun reconstruir(
            id: FuncionarioId,
            nome: String,
            cpf: CPF,
            cargo: String,
        ): Funcionario =
            Funcionario(
                id = id,
                nome = nome,
                cpf = cpf,
                cargo = Cargo.fromName(cargo),
            )
    }
}
