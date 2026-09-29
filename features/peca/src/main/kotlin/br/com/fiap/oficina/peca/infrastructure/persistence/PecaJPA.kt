package br.com.fiap.oficina.peca.infrastructure.persistence

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import java.math.BigDecimal
import java.util.UUID

@Entity
@Table(
    name = "pecas",
    indexes = [
        Index(name = "idx_codigo", columnList = "codigo", unique = true),
    ],
)
internal class PecaJPA(
    @Id
    val id: UUID,
    @Column(nullable = false, unique = true, length = 15)
    val codigo: String,
    @Column(nullable = false, length = 100)
    val nome: String,
    @Column(length = 255)
    val descricao: String,
    @Column(length = 100)
    val fabricante: String,
    @Column(length = 100)
    val fornecedor: String,
    @Column(name = "preco_de_compra", nullable = false, precision = 10, scale = 2)
    val precoCompra: BigDecimal,
    @Column(name = "preco_de_venda", nullable = false, precision = 10, scale = 2)
    val precoVenda: BigDecimal = BigDecimal.ZERO,
    @Column(name = "qtd_estoque", nullable = false)
    val quantidade: Int = 0,
    @Column(nullable = false)
    val ativo: Boolean,
)
