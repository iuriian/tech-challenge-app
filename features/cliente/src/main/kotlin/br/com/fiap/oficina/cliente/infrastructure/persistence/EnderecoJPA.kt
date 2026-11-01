package br.com.fiap.oficina.cliente.infrastructure.persistence

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "enderecos")
internal data class EnderecoJPA(
    @Id
    val id: UUID,
    @Column(nullable = false)
    val logradouro: String,
    @Column(nullable = false)
    val numero: String,
    @Column(nullable = true)
    val complemento: String? = null,
    @Column(nullable = false)
    val bairro: String,
    @Column(nullable = false)
    val cidade: String,
    @Column(nullable = false)
    val estado: String,
    @Column(nullable = false)
    val cep: String,
)
