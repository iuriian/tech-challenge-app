package br.com.fiap.oficina.funcionario.domain

import br.com.fiap.oficina.shared.domain.valueobject.CPF

@ConsistentCopyVisibility
data class Funcionario private constructor(val id: FuncionarioId, val nome: String, val cpf: CPF, val cargo: Cargo) {
    init {
        require(nome.isNotBlank()) { "Nome não pode ser vazio" }
    }

    companion object {
        fun criar(nome: String, cpf: CPF, cargo: String): Funcionario = Funcionario(
            id = FuncionarioId.generate(),
            nome = nome,
            cpf = cpf,
            cargo = Cargo.fromDescricao(cargo),
        )

        fun reconstruir(id: String, nome: String, cpf: CPF, cargo: String): Funcionario = Funcionario(
            id = FuncionarioId.toUUID(id),
            nome = nome,
            cpf = cpf,
            cargo = Cargo.fromDescricao(cargo),
        )
    }

    fun atualizar(nome: String, cpf: CPF, cargo: String): Funcionario = this.copy(
        nome = nome,
        cpf = cpf,
        cargo = Cargo.fromDescricao(cargo),
    )
}
