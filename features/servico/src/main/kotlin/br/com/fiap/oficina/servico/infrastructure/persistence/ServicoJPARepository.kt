package br.com.fiap.oficina.servico.infrastructure.persistence

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

internal interface ServicoJPARepository : JpaRepository<ServicoJPA, UUID> {
    fun findAllByAtivoTrue(): List<ServicoJPA>
}
