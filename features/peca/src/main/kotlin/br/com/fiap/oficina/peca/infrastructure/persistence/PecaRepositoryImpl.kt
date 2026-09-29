package br.com.fiap.oficina.peca.infrastructure.persistence

import br.com.fiap.oficina.peca.domain.Peca
import br.com.fiap.oficina.peca.domain.PecaId
import br.com.fiap.oficina.peca.domain.PecaRepository
import br.com.fiap.oficina.peca.infrastructure.mapper.PecaJPAMapper

internal class PecaRepositoryImpl(
    private val jpaRepository: PecaJPARepository,
    private val mapper: PecaJPAMapper,
) : PecaRepository {
    override fun salvar(peca: Peca): Peca {
        val resultado = jpaRepository.save(mapper.toJpa(peca))
        return mapper.toDomain(resultado)
    }

    override fun buscarPorCodigo(codigo: String): Peca? = jpaRepository.findByCodigo(codigo)?.let(mapper::toDomain)

    override fun buscarPorId(id: PecaId): Peca? = jpaRepository.findById(id.value).map(mapper::toDomain).orElse(null)

    override fun listar(): List<Peca> = jpaRepository.findAll().map(mapper::toDomain)
}
