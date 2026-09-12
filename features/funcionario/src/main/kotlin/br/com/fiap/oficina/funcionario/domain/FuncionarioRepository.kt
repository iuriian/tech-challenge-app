package br.com.fiap.oficina.funcionario.domain

/**
 * Contrato a ser implementado na camada de infraestrutura para persistência de dados.
 */
internal interface FuncionarioRepository {
    fun salvar(funcionario: Funcionario): Funcionario

    fun listarTodos(): List<Funcionario>

    fun buscarPorId(id: FuncionarioId): Funcionario?

    fun buscarPorCpf(cpf: String): Funcionario?

    fun buscarPorNome(nome: String): Funcionario?

    fun editar(funcionario: Funcionario): Funcionario

    fun deletar(id: FuncionarioId)
}
