package br.com.fiap.oficina.cliente.infrastructure.persistence

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

internal interface ClienteJPARepository : JpaRepository<ClienteJPA, UUID> {
    fun findByDocumentoNumero(numeroDocumento: String): ClienteJPA?

    fun findByNome(nome: String): ClienteJPA?
}
