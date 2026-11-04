package br.com.fiap.oficina.veiculo.infrastructure.persistence

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

internal interface VeiculoJPARepository : JpaRepository<VeiculoJPA, UUID> {
    fun findByPlaca(placa: String): VeiculoJPA?

    fun findByProprietarioId(id: UUID): List<VeiculoJPA>

    fun existsByPlaca(placa: String): Boolean
}
