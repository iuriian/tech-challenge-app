package br.com.fiap.oficina.veiculo.application.usecase

import br.com.fiap.oficina.veiculo.application.dto.VeiculoResponse

interface BuscarVeiculoPorIdUseCase {
    fun executar(id: String): VeiculoResponse
}
