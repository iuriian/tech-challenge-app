package br.com.fiap.oficina.funcionario.application.usecase

import br.com.fiap.oficina.funcionario.application.dto.FuncionarioResponse

interface BuscarFuncionarioPorNomeUseCase {
    fun executar(nome: String): FuncionarioResponse
}
