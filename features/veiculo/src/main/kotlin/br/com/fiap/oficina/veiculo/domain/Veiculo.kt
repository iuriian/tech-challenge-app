package br.com.fiap.oficina.veiculo.domain

import br.com.fiap.oficina.shared.domain.valueobject.Placa

@ConsistentCopyVisibility
data class Veiculo private constructor(
    val id: VeiculoId,
    val marca: String,
    val nome: String,
    val modelo: String,
    val ano: String,
    val placa: Placa,
    val proprietarioRef: ProprietarioRef,
) {
    companion object {
        fun criar(
            marca: String,
            nome: String,
            modelo: String,
            ano: String,
            placa: String,
            proprietarioRef: String,
        ): Veiculo {
            require(marca.isNotBlank()) { "Marca é obrigatória" }
            require(nome.isNotBlank()) { "Nome do veículo é obrigatório" }
            require(modelo.isNotBlank()) { "Modelo é obrigatório" }
            require(ano.isNotBlank()) { "Ano é obrigatório" }

            return Veiculo(
                id = VeiculoId.generate(),
                marca = marca,
                nome = nome,
                modelo = modelo,
                ano = ano,
                placa = Placa.criar(placa),
                proprietarioRef = ProprietarioRef.toUUID(proprietarioRef),
            )
        }

        fun reconstruir(
            id: String,
            marca: String,
            nome: String,
            modelo: String,
            ano: String,
            placa: String,
            proprietarioRef: String,
        ): Veiculo =
            Veiculo(
                id = VeiculoId.toUUID(id),
                marca = marca,
                nome = nome,
                modelo = modelo,
                ano = ano,
                placa = Placa.criar(placa),
                proprietarioRef = ProprietarioRef.toUUID(proprietarioRef),
            )
    }
}
