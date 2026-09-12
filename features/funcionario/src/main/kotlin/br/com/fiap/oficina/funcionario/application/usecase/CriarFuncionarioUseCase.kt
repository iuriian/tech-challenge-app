package br.com.fiap.oficina.funcionario.application.usecase

import br.com.fiap.oficina.funcionario.application.dto.FuncionarioRequest
import br.com.fiap.oficina.funcionario.application.dto.FuncionarioResponse
import br.com.fiap.oficina.funcionario.domain.Funcionario

/**
 * Contrato a ser implementado no FuncionarioController no módulo api
 */
interface CriarFuncionarioUseCase {
    fun executar(request: FuncionarioRequest): Result<FuncionarioResponse>
}
