package br.com.fiap.oficina.cliente.infrastructure.persistence

import br.com.fiap.oficina.cliente.domain.Cliente
import br.com.fiap.oficina.cliente.domain.ClienteId
import br.com.fiap.oficina.cliente.domain.ClienteRepository
import br.com.fiap.oficina.cliente.infrastructure.mapper.ClienteJPAMapper
import br.com.fiap.oficina.shared.domain.valueobject.Document

internal class ClienteRepositoryImpl(
    private val repository: ClienteJPARepository,
    private val mapper: ClienteJPAMapper,
) : ClienteRepository {
    override fun salvar(cliente: Cliente): Cliente {
        val resultado = repository.save(mapper.toJPA(cliente))
        return mapper.toDomain(resultado)
    }

    override fun listarTodos(): List<Cliente> = repository.findAll().map(mapper::toDomain)

    override fun buscarPorId(id: ClienteId): Cliente? = repository.findById(id.value).map(mapper::toDomain).orElse(null)

    override fun buscarPorDocumento(document: Document): Cliente? =
        repository.findByDocumentoNumero(document.valor)?.let(mapper::toDomain)

    override fun buscarPorNome(nome: String): Cliente? = repository.findByNome(nome)?.let(mapper::toDomain)

    override fun deletar(id: ClienteId) = repository.deleteById(id.value)
}
