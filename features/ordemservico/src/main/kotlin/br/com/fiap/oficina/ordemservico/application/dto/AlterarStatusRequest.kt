package br.com.fiap.oficina.ordemservico.application.dto

import br.com.fiap.oficina.ordemservico.domain.OrdemServicoStatus
import jakarta.validation.constraints.NotNull

data class AlterarStatusRequest(
    @field:NotNull
    val status: OrdemServicoStatus,
)
