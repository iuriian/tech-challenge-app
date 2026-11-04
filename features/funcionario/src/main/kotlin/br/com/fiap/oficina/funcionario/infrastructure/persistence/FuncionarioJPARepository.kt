package br.com.fiap.oficina.funcionario.infrastructure.persistence

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

internal interface FuncionarioJPARepository : JpaRepository<FuncionarioJPA, UUID> {
    fun findByNome(nome: String): FuncionarioJPA?

    fun findByCpf(cpf: String): FuncionarioJPA?
}
