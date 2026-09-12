package br.com.fiap.oficina.funcionario.domain

import java.util.UUID

class Funcionario private constructor(
    val id: FuncionarioId,
    val nome: String,
    val cpf: CPF,
    val cargo: Cargo,
) {
    init {
        require(nome.isNotBlank()) { "Nome não pode ser vazio" }
    }

    companion object {
        fun criar(
            nome: String,
            cpf: String,
            cargo: String,
        ): Funcionario =
            Funcionario(
                id = FuncionarioId.generate(),
                nome = nome,
                cpf = CPF(cpf),
                cargo = Cargo.fromName(cargo),
            )

        fun reconstruir(
            id: String,
            nome: String,
            cpf: String,
            cargo: String,
        ): Funcionario =
            Funcionario(
                id = FuncionarioId.toUUID(id),
                nome = nome,
                cpf = CPF(cpf),
                cargo = Cargo.fromName(cargo),
            )
    }
}
