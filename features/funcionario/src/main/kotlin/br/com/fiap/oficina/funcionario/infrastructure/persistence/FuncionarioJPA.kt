package br.com.fiap.oficina.funcionario.infrastructure.persistence

import br.com.fiap.oficina.funcionario.domain.Cargo
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(
    name = "funcionarios",
    indexes = [
        Index(name = "idx_cpf", columnList = "cpf", unique = true),
    ],
)
internal data class FuncionarioJPA(
    @Id
    val id: UUID,
    @Column(nullable = false, length = 100)
    val nome: String,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    val cargo: Cargo,
    @Column(nullable = false, unique = true, length = 11)
    val cpf: String,
)
