package br.com.fiap.oficina.funcionario.application.usecase

import br.com.fiap.oficina.funcionario.application.dto.FuncionarioRequest
import br.com.fiap.oficina.funcionario.application.dto.FuncionarioResponse

interface AtualizarFuncionarioUseCase {
    fun executar(
        id: String,
        request: FuncionarioRequest,
    ): FuncionarioResponse
}
