package br.com.fiap.oficina.ordemservico.infrastructure.persistence

import br.com.fiap.oficina.ordemservico.domain.OrdemServicoStatus
import org.springframework.data.jpa.repository.JpaRepository
import java.time.Instant
import java.util.UUID

internal interface OrdemServicoJPARepository : JpaRepository<OrdemServicoJPA, UUID> {
    fun findByStatus(status: OrdemServicoStatus): List<OrdemServicoJPA>

    fun findByClienteId(clienteId: UUID): List<OrdemServicoJPA>

    fun findByVeiculoId(veiculoId: UUID): List<OrdemServicoJPA>

    fun findByDataAberturaBetween(inicio: Instant, fim: Instant): List<OrdemServicoJPA>
}
