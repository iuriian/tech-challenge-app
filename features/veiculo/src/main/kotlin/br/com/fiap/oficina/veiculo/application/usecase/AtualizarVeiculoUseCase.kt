package br.com.fiap.oficina.veiculo.application.usecase

import br.com.fiap.oficina.veiculo.application.dto.VeiculoRequest
import br.com.fiap.oficina.veiculo.application.dto.VeiculoResponse

interface AtualizarVeiculoUseCase {
    fun executar(
        id: String,
        request: VeiculoRequest,
    ): VeiculoResponse
}
