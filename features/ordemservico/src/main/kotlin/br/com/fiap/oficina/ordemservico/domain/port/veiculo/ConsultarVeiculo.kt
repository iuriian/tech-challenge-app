package br.com.fiap.oficina.ordemservico.domain.port.veiculo

import br.com.fiap.oficina.ordemservico.domain.VeiculoRef

interface ConsultarVeiculo {
    fun buscarPorId(ref: VeiculoRef): DadosVeiculo?
}
