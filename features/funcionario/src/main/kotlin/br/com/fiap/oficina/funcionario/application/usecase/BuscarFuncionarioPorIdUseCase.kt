package br.com.fiap.oficina.funcionario.application.usecase

import br.com.fiap.oficina.funcionario.application.dto.FuncionarioResponse

interface BuscarFuncionarioPorIdUseCase {
    fun execute(id: String): FuncionarioResponse
}
