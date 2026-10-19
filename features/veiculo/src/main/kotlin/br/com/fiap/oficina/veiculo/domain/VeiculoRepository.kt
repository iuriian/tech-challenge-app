package br.com.fiap.oficina.veiculo.domain

import br.com.fiap.oficina.shared.domain.valueobject.Placa

internal interface VeiculoRepository {
    fun salvar(veiculo: Veiculo): Veiculo

    fun buscarPorId(id: VeiculoId): Veiculo?

    fun buscarPorPlaca(placa: Placa): Veiculo?

    fun buscarPorMotorista(id: ProprietarioRef): List<Veiculo>

    fun existeVeiculo(placa: Placa): Boolean

    fun listarTodos(): List<Veiculo>

    fun deletar(id: VeiculoId)
}
