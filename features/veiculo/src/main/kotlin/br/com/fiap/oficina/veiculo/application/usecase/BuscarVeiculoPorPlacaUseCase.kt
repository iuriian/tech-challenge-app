package br.com.fiap.oficina.veiculo.application.usecase

import br.com.fiap.oficina.veiculo.application.dto.VeiculoResponse

interface BuscarVeiculoPorPlacaUseCase {
    fun executar(placa: String): VeiculoResponse
}
