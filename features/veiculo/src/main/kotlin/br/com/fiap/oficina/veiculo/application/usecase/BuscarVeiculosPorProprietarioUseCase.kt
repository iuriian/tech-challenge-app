package br.com.fiap.oficina.veiculo.application.usecase

import br.com.fiap.oficina.veiculo.application.dto.VeiculoResponse

interface BuscarVeiculosPorProprietarioUseCase {
    fun executar(id: String): List<VeiculoResponse>
}
