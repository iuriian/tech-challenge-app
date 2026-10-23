package br.com.fiap.oficina.veiculo.application.mapper

import br.com.fiap.oficina.shared.domain.mapper.CompositeApplicationMapper
import br.com.fiap.oficina.veiculo.application.dto.ProprietarioResponse
import br.com.fiap.oficina.veiculo.application.dto.VeiculoRequest
import br.com.fiap.oficina.veiculo.application.dto.VeiculoResponse
import br.com.fiap.oficina.veiculo.domain.Veiculo
import br.com.fiap.oficina.veiculo.domain.port.DadosProprietario
import org.springframework.stereotype.Component

@Component
internal class VeiculoMapper : CompositeApplicationMapper<Veiculo, DadosProprietario, VeiculoRequest, VeiculoResponse> {
    override fun toDomain(request: VeiculoRequest): Veiculo = Veiculo.criar(
        marca = request.marca,
        nome = request.nome,
        modelo = request.modelo,
        ano = request.ano,
        placa = request.placa,
        proprietarioRef = request.proprietarioId,
    )

    override fun toResponse(domain: Veiculo, context: DadosProprietario?): VeiculoResponse = VeiculoResponse(
        id = domain.id.value.toString(),
        nome = domain.nome,
        marca = domain.marca,
        modelo = domain.modelo,
        ano = domain.ano,
        placa = domain.placa.value,
        proprietario =
        context?.let {
            ProprietarioResponse(
                id = it.ref.value.toString(),
                nome = it.nome,
                numeroDocumento = it.numeroDocumento,
            )
        },
    )
}
