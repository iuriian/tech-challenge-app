package br.com.fiap.oficina.servico.infrastructure.persistence

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
import java.util.UUID

@Entity
@Table(name = "catalogo_servicos")
internal data class ServicoJPA(
    @Id
    val id: UUID,
    @Column(nullable = false, length = 100)
    val descricao: String,
    @Column(nullable = false, precision = 10, scale = 2)
    val valor: BigDecimal,
    @Column(nullable = false)
    val ativo: Boolean,
)
