package br.com.fiap.oficina.funcionario.domain

internal interface FuncionarioRepository {
    fun salvar(funcionario: Funcionario): Funcionario

    fun listarTodos(): List<Funcionario>

    fun buscarPorId(id: FuncionarioId): Funcionario?

    fun buscarPorNome(nome: String): Funcionario?

    fun editar(funcionario: Funcionario): Funcionario

    fun deletar(id: FuncionarioId)
}
