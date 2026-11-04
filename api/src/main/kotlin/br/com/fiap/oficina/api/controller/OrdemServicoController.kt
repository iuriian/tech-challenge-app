package br.com.fiap.oficina.api.controller

import br.com.fiap.oficina.api.contract.OrdemServicoApi
import br.com.fiap.oficina.api.observability.OrdemServicoTelemetry
import br.com.fiap.oficina.ordemservico.application.dto.AlterarStatusRequest
import br.com.fiap.oficina.ordemservico.application.dto.OrcamentoResponse
import br.com.fiap.oficina.ordemservico.application.dto.OrdemServicoRequest
import br.com.fiap.oficina.ordemservico.application.dto.OrdemServicoResponse
import br.com.fiap.oficina.ordemservico.application.dto.TempoMedioExecucaoResponse
import br.com.fiap.oficina.ordemservico.application.usecase.AlterarStatusOrdemServicoUseCase
import br.com.fiap.oficina.ordemservico.application.usecase.AtualizarOrdemServicoUseCase
import br.com.fiap.oficina.ordemservico.application.usecase.AvancarStatusOrdemServicoUseCase
import br.com.fiap.oficina.ordemservico.application.usecase.BuscarOrdemServicoPorClienteUseCase
import br.com.fiap.oficina.ordemservico.application.usecase.BuscarOrdemServicoPorIdUseCase
import br.com.fiap.oficina.ordemservico.application.usecase.CalcularTempoMedioExecucaoUseCase
import br.com.fiap.oficina.ordemservico.application.usecase.CriarOrdemServicoUseCase
import br.com.fiap.oficina.ordemservico.application.usecase.DeletarOrdemServicoUseCase
import br.com.fiap.oficina.ordemservico.application.usecase.ListarOrdemServicoUseCase
import br.com.fiap.oficina.ordemservico.application.usecase.ObterOrcamentoUseCase
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
internal class OrdemServicoController(
    private val criarOrdemServicoUseCase: CriarOrdemServicoUseCase,
    private val listarOrdemServicoUseCase: ListarOrdemServicoUseCase,
    private val buscarOrdemServicoPorIdUseCase: BuscarOrdemServicoPorIdUseCase,
    private val buscarOrdemServicoPorClienteUseCase: BuscarOrdemServicoPorClienteUseCase,
    private val obterOrcamentoUseCase: ObterOrcamentoUseCase,
    private val calcularTempoMedioExecucaoUseCase: CalcularTempoMedioExecucaoUseCase,
    private val atualizarOrdemServicoUseCase: AtualizarOrdemServicoUseCase,
    private val avancarStatusOrdemServicoUseCase: AvancarStatusOrdemServicoUseCase,
    private val alterarStatusOrdemServicoUseCase: AlterarStatusOrdemServicoUseCase,
    private val deletarOrdemServicoUseCase: DeletarOrdemServicoUseCase,
    private val telemetry: OrdemServicoTelemetry,
) : OrdemServicoApi {
    override fun criar(request: OrdemServicoRequest): ResponseEntity<OrdemServicoResponse> = try {
        val response = criarOrdemServicoUseCase.executar(request)
        telemetry.registrarCriacao(response)
        ResponseEntity.status(HttpStatus.CREATED).body(response)
    } catch (exception: RuntimeException) {
        telemetry.registrarFalha(
            action = OrdemServicoTelemetry.ACTION_CRIADA,
            ordemId = null,
            erro = exception,
        )
        throw exception
    }

    override fun listarTodos(): List<OrdemServicoResponse> = listarOrdemServicoUseCase.executar()

    override fun buscarPorId(id: String): OrdemServicoResponse = buscarOrdemServicoPorIdUseCase.executar(id)

    override fun listarPorCliente(clienteId: String): List<OrdemServicoResponse> =
        buscarOrdemServicoPorClienteUseCase.executar(clienteId)

    override fun obterOrcamento(id: String): OrcamentoResponse = obterOrcamentoUseCase.executar(id)

    override fun tempoMedioExecucao(): TempoMedioExecucaoResponse = calcularTempoMedioExecucaoUseCase.executar()

    override fun atualizar(id: String, request: OrdemServicoRequest): OrdemServicoResponse =
        atualizarOrdemServicoUseCase.executar(id, request)

    override fun avancarStatus(id: String): OrdemServicoResponse = try {
        avancarStatusOrdemServicoUseCase.executar(id).also { response ->
            telemetry.registrarMudancaStatus(
                action = OrdemServicoTelemetry.ACTION_AVANCADA,
                response = response,
            )
        }
    } catch (exception: RuntimeException) {
        telemetry.registrarFalha(
            action = OrdemServicoTelemetry.ACTION_AVANCADA,
            ordemId = id,
            erro = exception,
        )
        throw exception
    }

    override fun alterarStatus(id: String, request: AlterarStatusRequest): OrdemServicoResponse = try {
        alterarStatusOrdemServicoUseCase.executar(id, request).also { response ->
            telemetry.registrarMudancaStatus(
                action = OrdemServicoTelemetry.ACTION_STATUS_ALTERADO,
                response = response,
            )
        }
    } catch (exception: RuntimeException) {
        telemetry.registrarFalha(
            action = OrdemServicoTelemetry.ACTION_STATUS_ALTERADO,
            ordemId = id,
            status = request.status.name,
            erro = exception,
        )
        throw exception
    }

    override fun deletar(id: String) = deletarOrdemServicoUseCase.executar(id)
}
