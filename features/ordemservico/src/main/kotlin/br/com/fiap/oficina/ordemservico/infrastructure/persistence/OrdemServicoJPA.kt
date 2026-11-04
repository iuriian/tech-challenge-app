package br.com.fiap.oficina.ordemservico.infrastructure.persistence

import br.com.fiap.oficina.ordemservico.domain.OrdemServicoStatus
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "servicos")
internal data class OrdemServicoJPA(
    @Id
    val id: UUID,
    /**
     * Permanece nulável por compatibilidade com as ordens de serviço anteriores à
     * migração `V11`, que não possuem número de negócio.
     */
    @Column(name = "os_number", length = 50)
    val osNumber: String? = null,
    @Column(nullable = false, length = 100)
    val descricao: String,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    val status: OrdemServicoStatus,
    @Column(name = "funcionario_id", nullable = false)
    val funcionarioId: UUID,
    @Column(name = "cliente_id", nullable = false)
    val clienteId: UUID,
    @Column(name = "veiculo_id", nullable = false)
    val veiculoId: UUID,
    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "ordem_servico_id", nullable = false)
    val itens: List<ItemOrcamentoJPA> = emptyList(),
    @Column(name = "prazo_minutos")
    val prazoMinutos: Long? = null,
    @Column(name = "data_abertura", nullable = false)
    val dataAbertura: Instant,
    @Column(name = "data_inicio_execucao")
    val dataInicioExecucao: Instant? = null,
    @Column(name = "data_finalizacao")
    val dataFinalizacao: Instant? = null,
)
