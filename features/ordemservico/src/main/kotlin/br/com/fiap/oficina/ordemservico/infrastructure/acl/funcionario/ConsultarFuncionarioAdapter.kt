package br.com.fiap.oficina.ordemservico.infrastructure.acl.funcionario

import br.com.fiap.oficina.funcionario.application.port.FuncionarioQueryPort
import br.com.fiap.oficina.ordemservico.domain.FuncionarioRef
import br.com.fiap.oficina.ordemservico.domain.port.funcionario.ConsultarFuncionario
import br.com.fiap.oficina.ordemservico.domain.port.funcionario.DadosFuncionario

internal class ConsultarFuncionarioAdapter(
    private val queryPort: FuncionarioQueryPort,
    private val mapper: FuncionarioMapper,
) : ConsultarFuncionario {
    override fun buscarPorId(ref: FuncionarioRef): DadosFuncionario? =
        queryPort.buscarPorId(ref.value)?.let(mapper::toProviderResponse)
}
