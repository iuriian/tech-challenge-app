package br.com.fiap.oficina.funcionario.domain

class FuncionarioNaoEncontradoException(
    message: String,
) : RuntimeException(message) {
    companion object {
        fun porId(id: String): FuncionarioNaoEncontradoException =
            FuncionarioNaoEncontradoException("Funcionário não encontrado com o ID: $id")

        fun porNome(nome: String): FuncionarioNaoEncontradoException =
            FuncionarioNaoEncontradoException("Funcionário não encontrado com o nome: $nome")
    }
}
