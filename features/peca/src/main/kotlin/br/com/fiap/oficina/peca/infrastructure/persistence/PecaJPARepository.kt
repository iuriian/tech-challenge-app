package br.com.fiap.oficina.peca.infrastructure.persistence

import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

internal interface PecaJPARepository : JpaRepository<PecaJPA, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from PecaJPA p where p.codigo = :codigo")
    fun findByCodigo(
        @Param("codigo") codigo: String,
    ): PecaJPA?
}
