package br.com.fiap.oficina.ordemservico.infrastructure.persistence

import br.com.fiap.oficina.ordemservico.domain.TipoItemOrcamento
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
import java.util.UUID

@Entity
@Table(name = "itens_orcamento")
internal data class ItemOrcamentoJPA(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    val tipo: TipoItemOrcamento,
    @Column(name = "referencia_id", nullable = false)
    val referenciaId: UUID,
    @Column(nullable = false, length = 100)
    val descricao: String,
    @Column(name = "valor_unitario", nullable = false, precision = 10, scale = 2)
    val valorUnitario: BigDecimal,
    @Column(nullable = false, precision = 10, scale = 2)
    val quantidade: BigDecimal,
    @Column(name = "codigo_referencia", length = 50)
    val codigoReferencia: String? = null,
)
