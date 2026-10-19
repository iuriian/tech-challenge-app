package br.com.fiap.oficina.veiculo.infrastructure.persistence

import br.com.fiap.oficina.shared.domain.valueobject.Placa
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

internal interface VeiculoJPARepository : JpaRepository<VeiculoJPA, UUID> {
    fun findByPlaca(placa: String): VeiculoJPA?

    fun findByProprietarioId(id: UUID): List<VeiculoJPA>

    fun exist(placa: String): Boolean
}
