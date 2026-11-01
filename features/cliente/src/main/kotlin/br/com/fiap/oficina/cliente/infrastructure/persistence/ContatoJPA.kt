package br.com.fiap.oficina.cliente.infrastructure.persistence

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "contatos")
internal data class ContatoJPA(
    @Id
    val id: UUID,
    @Column(nullable = false)
    val tipo: String,
    @Column(nullable = false)
    val nome: String,
    @Column(nullable = false)
    val telefone: String,
)
