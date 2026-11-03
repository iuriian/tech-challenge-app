package br.com.fiap.oficina.ordemservico.domain.port.funcionario

import br.com.fiap.oficina.ordemservico.domain.FuncionarioRef

interface ConsultarFuncionario {
    fun buscarPorId(ref: FuncionarioRef): DadosFuncionario?
}
