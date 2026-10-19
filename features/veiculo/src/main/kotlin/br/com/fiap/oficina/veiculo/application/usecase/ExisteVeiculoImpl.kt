package br.com.fiap.oficina.veiculo.application.usecase

import br.com.fiap.oficina.shared.domain.valueobject.Placa
import br.com.fiap.oficina.veiculo.application.mapper.VeiculoMapper
import br.com.fiap.oficina.veiculo.domain.VeiculoRepository
import org.springframework.stereotype.Service

@Service
internal data class ExisteVeiculoImpl(
    private val repository: VeiculoRepository,
    private val mapper: VeiculoMapper,
) : ExisteVeiculo {
    override fun executar(placa: Placa): Boolean = repository.existeVeiculo(placa)
}
