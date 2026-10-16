package br.com.fiap.oficina.veiculo.application.mapper

import br.com.fiap.oficina.shared.application.mapper.ApplicationMapper
import br.com.fiap.oficina.veiculo.application.dto.VeiculoRequest
import br.com.fiap.oficina.veiculo.application.dto.VeiculoResponse
import br.com.fiap.oficina.veiculo.domain.Veiculo
import org.springframework.stereotype.Component

@Component
internal class VeiculoMapper : ApplicationMapper<Veiculo, VeiculoRequest, VeiculoResponse> {
    override fun toDomain(request: VeiculoRequest): Veiculo =
        Veiculo.criar(
            marca = request.marca,
            nome = request.nome,
            modelo = request.modelo,
            ano = request.ano,
            placa = request.placa,
            proprietarioId = request.proprietarioId,
        )

    override fun toResponse(domain: Veiculo): VeiculoResponse =
        VeiculoResponse(
            id = domain.id.value.toString(),
            nome = domain.nome,
            marca = domain.marca,
            modelo = domain.modelo,
            ano = domain.ano,
            placa = domain.placa,
            proprietarioId = domain.proprietarioId.value.toString(),
        )
}
