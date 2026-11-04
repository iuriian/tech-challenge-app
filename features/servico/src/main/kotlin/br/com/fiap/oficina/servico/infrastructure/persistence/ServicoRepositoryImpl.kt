package br.com.fiap.oficina.servico.infrastructure.persistence

import br.com.fiap.oficina.servico.domain.Servico
import br.com.fiap.oficina.servico.domain.ServicoId
import br.com.fiap.oficina.servico.domain.ServicoRepository
import br.com.fiap.oficina.servico.infrastructure.mapper.ServicoJPAMapper

internal class ServicoRepositoryImpl(
    private val jpaRepository: ServicoJPARepository,
    private val mapper: ServicoJPAMapper,
) : ServicoRepository {
    override fun salvar(servico: Servico): Servico = mapper.toDomain(jpaRepository.save(mapper.toJPA(servico)))

    override fun buscarPorId(id: ServicoId): Servico? = jpaRepository
        .findById(id.value)
        .map(mapper::toDomain)
        .orElse(null)

    override fun listarAtivos(): List<Servico> = jpaRepository.findAllByAtivoTrue().map(mapper::toDomain)

    override fun listarTodos(): List<Servico> = jpaRepository.findAll().map(mapper::toDomain)
}
