package br.com.fiap.oficina.ordemservico.domain.port.cliente

import br.com.fiap.oficina.ordemservico.domain.ClienteRef

interface ConsultarCliente {
    fun buscarPorId(ref: ClienteRef): DadosCliente?
}
