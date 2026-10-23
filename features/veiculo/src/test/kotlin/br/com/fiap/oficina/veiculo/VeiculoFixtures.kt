package br.com.fiap.oficina.veiculo

import br.com.fiap.oficina.cliente.application.port.ClienteQueryDto
import br.com.fiap.oficina.veiculo.application.dto.VeiculoRequest
import br.com.fiap.oficina.veiculo.domain.ProprietarioRef
import br.com.fiap.oficina.veiculo.domain.Veiculo
import br.com.fiap.oficina.veiculo.domain.port.DadosProprietario
import br.com.fiap.oficina.veiculo.infrastructure.persistence.VeiculoJPA
import java.util.UUID

/** Massa de teste compartilhada pelos testes de veículo. */
internal object VeiculoFixtures {
    const val ID_1 = "00000000-0000-0000-0000-000000000100"
    const val ID_2 = "00000000-0000-0000-0000-000000000200"
    const val PROPRIETARIO_ID = "00000000-0000-0000-0000-000000000300"
    const val PROPRIETARIO_ID_2 = "00000000-0000-0000-0000-000000000400"

    const val PLACA = "ABC1234"
    const val PLACA_MERCOSUL = "ABC1D23"
    const val PLACA_OUTRA = "XYZ9876"

    const val MARCA = "Honda"
    const val NOME = "Civic"
    const val MODELO = "EXL 2.0"
    const val ANO = "2020"

    const val PROPRIETARIO_NOME = "João Silva"
    const val PROPRIETARIO_DOCUMENTO = "01234567890"

    fun veiculo(
        id: String = ID_1,
        marca: String = MARCA,
        nome: String = NOME,
        modelo: String = MODELO,
        ano: String = ANO,
        placa: String = PLACA,
        proprietarioRef: String = PROPRIETARIO_ID,
    ) = Veiculo.reconstruir(
        id = id,
        marca = marca,
        nome = nome,
        modelo = modelo,
        ano = ano,
        placa = placa,
        proprietarioRef = proprietarioRef,
    )

    fun novoVeiculo(
        marca: String = MARCA,
        nome: String = NOME,
        modelo: String = MODELO,
        ano: String = ANO,
        placa: String = PLACA,
        proprietarioRef: String = PROPRIETARIO_ID,
    ) = Veiculo.criar(
        marca = marca,
        nome = nome,
        modelo = modelo,
        ano = ano,
        placa = placa,
        proprietarioRef = proprietarioRef,
    )

    fun veiculoRequest(
        marca: String = MARCA,
        nome: String = NOME,
        modelo: String = MODELO,
        ano: String = ANO,
        placa: String = PLACA,
        proprietarioId: String = PROPRIETARIO_ID,
    ) = VeiculoRequest(
        nome = nome,
        marca = marca,
        modelo = modelo,
        ano = ano,
        placa = placa,
        proprietarioId = proprietarioId,
    )

    fun veiculoJPA(
        id: String = ID_1,
        marca: String = MARCA,
        nome: String = NOME,
        modelo: String = MODELO,
        ano: String = ANO,
        placa: String = PLACA,
        proprietarioId: String = PROPRIETARIO_ID,
    ) = VeiculoJPA(
        id = UUID.fromString(id),
        marca = marca,
        nome = nome,
        modelo = modelo,
        ano = ano,
        placa = placa,
        proprietarioId = UUID.fromString(proprietarioId),
    )

    fun dadosProprietario(
        id: String = PROPRIETARIO_ID,
        nome: String = PROPRIETARIO_NOME,
        numeroDocumento: String = PROPRIETARIO_DOCUMENTO,
    ) = DadosProprietario(
        ref = ProprietarioRef.toUUID(id),
        nome = nome,
        numeroDocumento = numeroDocumento,
    )

    fun clienteQueryDto(
        id: String = PROPRIETARIO_ID,
        nome: String = PROPRIETARIO_NOME,
        numeroDocumento: String = PROPRIETARIO_DOCUMENTO,
    ) = ClienteQueryDto(
        id = id,
        nome = nome,
        numeroDocumento = numeroDocumento,
    )
}
