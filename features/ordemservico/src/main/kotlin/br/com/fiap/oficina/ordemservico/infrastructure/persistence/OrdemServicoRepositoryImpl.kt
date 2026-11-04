package br.com.fiap.oficina.ordemservico.infrastructure.persistence

import br.com.fiap.oficina.ordemservico.domain.ClienteRef
import br.com.fiap.oficina.ordemservico.domain.OrdemServico
import br.com.fiap.oficina.ordemservico.domain.OrdemServicoId
import br.com.fiap.oficina.ordemservico.domain.OrdemServicoRepository
import br.com.fiap.oficina.ordemservico.domain.OrdemServicoStatus
import br.com.fiap.oficina.ordemservico.domain.VeiculoRef
import br.com.fiap.oficina.ordemservico.infrastructure.mapper.OrdemServicoJPAMapper
import java.time.Instant

internal class OrdemServicoRepositoryImpl(
    private val jpaRepository: OrdemServicoJPARepository,
    private val mapper: OrdemServicoJPAMapper,
) : OrdemServicoRepository {
    override fun salvar(ordemServico: OrdemServico): OrdemServico =
        mapper.toDomain(jpaRepository.save(mapper.toJPA(ordemServico)))

    override fun buscarPorId(id: OrdemServicoId): OrdemServico? = jpaRepository
        .findById(id.value)
        .map(mapper::toDomain)
        .orElse(null)

    override fun listarTodos(): List<OrdemServico> = jpaRepository.findAll().map(mapper::toDomain)

    override fun buscarPorStatus(status: OrdemServicoStatus): List<OrdemServico> = jpaRepository
        .findByStatus(status)
        .map(mapper::toDomain)

    override fun buscarPorCliente(clienteId: ClienteRef): List<OrdemServico> = jpaRepository
        .findByClienteId(clienteId.value)
        .map(mapper::toDomain)

    override fun buscarPorVeiculo(veiculoId: VeiculoRef): List<OrdemServico> = jpaRepository
        .findByVeiculoId(veiculoId.value)
        .map(mapper::toDomain)

    override fun listarPorDataAberturaEntre(inicio: Instant, fim: Instant): List<OrdemServico> = jpaRepository
        .findByDataAberturaBetween(inicio, fim)
        .map(mapper::toDomain)

    override fun deletar(id: OrdemServicoId): Boolean {
        if (!jpaRepository.existsById(id.value)) {
            return false
        }

        jpaRepository.deleteById(id.value)

        return true
    }
}
