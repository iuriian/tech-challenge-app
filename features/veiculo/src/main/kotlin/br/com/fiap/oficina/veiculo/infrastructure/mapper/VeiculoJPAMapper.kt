package br.com.fiap.oficina.veiculo.infrastructure.mapper

import br.com.fiap.oficina.shared.infrastructure.mapper.JPAMapper
import br.com.fiap.oficina.veiculo.domain.Veiculo
import br.com.fiap.oficina.veiculo.infrastructure.persistence.VeiculoJPA

internal class VeiculoJPAMapper : JPAMapper<Veiculo, VeiculoJPA> {
    override fun toDomain(jpa: VeiculoJPA): Veiculo =
        Veiculo.reconstruir(
            id = jpa.id.toString(),
            marca = jpa.marca,
            nome = jpa.nome,
            modelo = jpa.modelo,
            ano = jpa.ano,
            placa = jpa.placa,
            proprietarioId = jpa.proprietarioId.toString(),
        )

    override fun toJPA(domain: Veiculo): VeiculoJPA =
        VeiculoJPA(
            id = domain.id.value,
            marca = domain.marca,
            nome = domain.nome,
            modelo = domain.modelo,
            ano = domain.ano,
            placa = domain.placa,
            proprietarioId = domain.proprietarioId.value,
        )
}
