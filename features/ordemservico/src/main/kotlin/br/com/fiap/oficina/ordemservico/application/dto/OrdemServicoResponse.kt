package br.com.fiap.oficina.ordemservico.application.dto

import br.com.fiap.oficina.ordemservico.domain.OrdemServicoStatus
import java.time.Instant

data class OrdemServicoResponse(
    val id: String,
    val descricao: String,
    val status: OrdemServicoStatus,
    val funcionarioId: String,
    val clienteId: String,
    val veiculoId: String,
    val itens: List<ItemOrcamentoResponse>,
    val dataAbertura: Instant,
    val dataInicioExecucao: Instant?,
    val dataFinalizacao: Instant?,
)
