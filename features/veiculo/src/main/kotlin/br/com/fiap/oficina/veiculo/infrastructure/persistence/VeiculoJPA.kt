package br.com.fiap.oficina.veiculo.infrastructure.persistence

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.Size
import java.util.UUID

@Entity
@Table(name = "veiculos")
internal data class VeiculoJPA(
    @Id
    val id: UUID,
    @Column(nullable = false)
    val marca: String,
    @Column(nullable = false)
    val nome: String,
    @Column(nullable = false)
    val modelo: String,
    @Column(nullable = false)
    val ano: String,
    @Column(nullable = false, unique = true)
    @Size(min = 7, max = 7, message = "Input must be exactly 7 characters long")
    val placa: String,
    @Column(name = "motorista_id", nullable = false)
    val proprietarioId: UUID,
)
