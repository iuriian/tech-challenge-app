package br.com.fiap.oficina.ordemservico.application.usecase

import br.com.fiap.oficina.ordemservico.application.dto.TempoMedioExecucaoResponse
import br.com.fiap.oficina.ordemservico.domain.OrdemServico
import br.com.fiap.oficina.ordemservico.domain.OrdemServicoRepository
import org.springframework.stereotype.Service

@Service
internal class CalcularTempoMedioExecucaoUseCaseImpl(private val repository: OrdemServicoRepository) :
    CalcularTempoMedioExecucaoUseCase {
    override fun executar(): TempoMedioExecucaoResponse {
        val temposExecucao = repository.listarTodos().mapNotNull(OrdemServico::tempoExecucaoEmMinutos)

        return TempoMedioExecucaoResponse(
            totalOrdensFinalizadas = temposExecucao.size,
            tempoMedioMinutos = temposExecucao.takeIf { it.isNotEmpty() }?.average(),
        )
    }
}
