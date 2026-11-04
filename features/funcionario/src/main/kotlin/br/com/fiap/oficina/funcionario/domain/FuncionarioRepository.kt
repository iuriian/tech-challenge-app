package br.com.fiap.oficina.funcionario.domain

import br.com.fiap.oficina.shared.domain.valueobject.CPF

/**
 * Contrato a ser implementado na camada de infraestrutura para persistência de dados.
 */
internal interface FuncionarioRepository {
    fun salvar(funcionario: Funcionario): Funcionario

    fun listarTodos(): List<Funcionario>

    fun buscarPorId(id: FuncionarioId): Funcionario?

    fun buscarPorCpf(cpf: CPF): Funcionario?

    fun buscarPorNome(nome: String): Funcionario?

    fun deletar(id: FuncionarioId)
}
