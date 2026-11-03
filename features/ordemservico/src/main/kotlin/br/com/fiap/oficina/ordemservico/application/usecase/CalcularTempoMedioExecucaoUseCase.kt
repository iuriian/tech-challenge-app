package br.com.fiap.oficina.ordemservico.application.usecase

import br.com.fiap.oficina.ordemservico.application.dto.TempoMedioExecucaoResponse

interface CalcularTempoMedioExecucaoUseCase {
    fun executar(): TempoMedioExecucaoResponse
}
