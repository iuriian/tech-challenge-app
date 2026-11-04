package br.com.fiap.oficina.veiculo.application.usecase

import br.com.fiap.oficina.veiculo.application.dto.VeiculoResponse

interface ListarVeiculosUseCase {
    fun executar(): List<VeiculoResponse>
}
