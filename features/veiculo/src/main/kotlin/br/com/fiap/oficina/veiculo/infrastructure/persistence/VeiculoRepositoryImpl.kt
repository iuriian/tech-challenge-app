package br.com.fiap.oficina.veiculo.infrastructure.persistence

import br.com.fiap.oficina.shared.domain.exception.BusinessRuleException
import br.com.fiap.oficina.shared.domain.valueobject.Placa
import br.com.fiap.oficina.veiculo.domain.ProprietarioRef
import br.com.fiap.oficina.veiculo.domain.Veiculo
import br.com.fiap.oficina.veiculo.domain.VeiculoId
import br.com.fiap.oficina.veiculo.domain.VeiculoRepository
import br.com.fiap.oficina.veiculo.infrastructure.mapper.VeiculoJPAMapper
import org.springframework.dao.DataIntegrityViolationException

internal class VeiculoRepositoryImpl(
    private val repository: VeiculoJPARepository,
    private val mapper: VeiculoJPAMapper,
) : VeiculoRepository {
    override fun salvar(veiculo: Veiculo): Veiculo =
        try {
            mapper.toDomain(repository.saveAndFlush(mapper.toJPA(veiculo)))
        } catch (e: DataIntegrityViolationException) {
            throw BusinessRuleException("Já existe veículo cadastrado com a placa ${veiculo.placa}", e)
        }

    override fun listarTodos(): List<Veiculo> = repository.findAll().map(mapper::toDomain)

    override fun buscarPorId(id: VeiculoId): Veiculo? =
        repository
            .findById(id.value)
            .map(mapper::toDomain)
            .orElse(null)

    override fun buscarPorPlaca(placa: Placa): Veiculo? = repository.findByPlaca(placa.value)?.let(mapper::toDomain)

    override fun buscarPorProprietario(id: ProprietarioRef): List<Veiculo> =
        repository.findByProprietarioId(id.value).map(mapper::toDomain)

    override fun deletar(id: VeiculoId) = repository.deleteById(id.value)

    override fun existeVeiculo(placa: Placa): Boolean = repository.existsByPlaca(placa.value)
}
