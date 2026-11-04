package br.com.fiap.oficina.api.controller

import br.com.fiap.oficina.api.contract.ServicoApi
import br.com.fiap.oficina.servico.application.dto.ServicoRequest
import br.com.fiap.oficina.servico.application.dto.ServicoResponse
import br.com.fiap.oficina.servico.application.usecase.AtualizarServicoUseCase
import br.com.fiap.oficina.servico.application.usecase.BuscarServicoUseCase
import br.com.fiap.oficina.servico.application.usecase.CriarServicoUseCase
import br.com.fiap.oficina.servico.application.usecase.DesativarServicoUseCase
import br.com.fiap.oficina.servico.application.usecase.ListarServicosAtivosUseCase
import br.com.fiap.oficina.servico.application.usecase.ListarTodosServicosUseCase
import br.com.fiap.oficina.servico.application.usecase.ReativarServicoUseCase
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
internal class ServicoController(
    private val criarServicoUseCase: CriarServicoUseCase,
    private val listarServicosAtivosUseCase: ListarServicosAtivosUseCase,
    private val listarTodosServicosUseCase: ListarTodosServicosUseCase,
    private val buscarServicoUseCase: BuscarServicoUseCase,
    private val atualizarServicoUseCase: AtualizarServicoUseCase,
    private val desativarServicoUseCase: DesativarServicoUseCase,
    private val reativarServicoUseCase: ReativarServicoUseCase,
) : ServicoApi {
    override fun criar(request: ServicoRequest): ResponseEntity<ServicoResponse> {
        val response = criarServicoUseCase.executar(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    override fun listarAtivos(): List<ServicoResponse> = listarServicosAtivosUseCase.executar()

    override fun listarTodos(): List<ServicoResponse> = listarTodosServicosUseCase.executar()

    override fun buscar(id: String): ServicoResponse = buscarServicoUseCase.executar(id)

    override fun atualizar(id: String, request: ServicoRequest): ServicoResponse =
        atualizarServicoUseCase.executar(id, request)

    override fun desativar(id: String) = desativarServicoUseCase.executar(id)

    override fun reativar(id: String): ServicoResponse = reativarServicoUseCase.executar(id)
}
