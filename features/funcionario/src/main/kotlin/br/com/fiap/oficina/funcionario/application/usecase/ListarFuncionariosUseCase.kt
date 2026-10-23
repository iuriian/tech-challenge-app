package br.com.fiap.oficina.funcionario.application.usecase

import br.com.fiap.oficina.funcionario.application.dto.FuncionarioResponse

interface ListarFuncionariosUseCase {
    fun executar(): List<FuncionarioResponse>
}
