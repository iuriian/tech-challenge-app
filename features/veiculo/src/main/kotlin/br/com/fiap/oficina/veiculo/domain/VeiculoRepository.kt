package br.com.fiap.oficina.veiculo.domain

internal interface VeiculoRepository {
    fun salvar(veiculo: Veiculo): Veiculo

    fun buscarPorId(id: VeiculoId): Veiculo?

    fun buscarPorPlaca(placa: String): Veiculo?

    fun buscarPorMotorista(id: ProprietarioId): List<Veiculo>

    fun listarTodos(): List<Veiculo>

    fun deletar(id: VeiculoId)
}
