package br.com.fiap.oficina.cliente.domain

import br.com.fiap.oficina.shared.domain.valueobject.Document

internal interface ClienteRepository {
    fun salvar(cliente: Cliente): Cliente

    fun buscarPorId(id: ClienteId): Cliente?

    fun buscarPorDocumento(document: Document): Cliente?

    fun buscarPorNome(nome: String): Cliente?

    fun listarTodos(): List<Cliente>

    fun deletar(id: ClienteId)
}
