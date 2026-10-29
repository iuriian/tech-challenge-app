package br.com.fiap.oficina.api.controller

import br.com.fiap.oficina.api.contract.PecaApi
import br.com.fiap.oficina.peca.application.dto.EstoquePecaRequest
import br.com.fiap.oficina.peca.application.dto.PecaRequest
import br.com.fiap.oficina.peca.application.dto.PecaResponse
import br.com.fiap.oficina.peca.application.dto.StatusPecaRequest
import br.com.fiap.oficina.peca.application.usecase.AtualizarPecaUseCase
import br.com.fiap.oficina.peca.application.usecase.AtualizarStatusPecaUseCase
import br.com.fiap.oficina.peca.application.usecase.BuscarPecaPorCodigoUseCase
import br.com.fiap.oficina.peca.application.usecase.CadastrarPecaUseCase
import br.com.fiap.oficina.peca.application.usecase.EstoquePecasUseCase
import br.com.fiap.oficina.peca.application.usecase.ListarPecasUseCase
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
internal class PecaController(
    private val cadastrarPecaUseCase: CadastrarPecaUseCase,
    private val atualizarPecaUseCase: AtualizarPecaUseCase,
    private val listarPecasUseCase: ListarPecasUseCase,
    private val buscarPecaPorCodigoUseCase: BuscarPecaPorCodigoUseCase,
    private val atualizarStatusPecaUseCase: AtualizarStatusPecaUseCase,
    private val estoquePecasUseCase: EstoquePecasUseCase,
) : PecaApi {
    override fun cadastrar(request: PecaRequest): ResponseEntity<PecaResponse> {
        val response = cadastrarPecaUseCase.executar(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    override fun atualizar(codigo: String, request: PecaRequest): PecaResponse =
        atualizarPecaUseCase.executar(codigo, request)

    override fun desativarPeca(codigo: String) {
        atualizarStatusPecaUseCase.executar(codigo, StatusPecaRequest(ativo = false))
    }

    override fun listar(): List<PecaResponse> = listarPecasUseCase.executar()

    override fun buscarPorCodigo(codigo: String): PecaResponse = buscarPecaPorCodigoUseCase.executar(codigo)

    override fun reativar(codigo: String) = atualizarStatusPecaUseCase.executar(codigo, StatusPecaRequest(ativo = true))

    override fun reporPecas(codigo: String, qtd: Int): PecaResponse =
        estoquePecasUseCase.executar(codigo, EstoquePecaRequest(quantidade = qtd))

    override fun retirarPecas(codigo: String, qtd: Int): PecaResponse =
        estoquePecasUseCase.executar(codigo, EstoquePecaRequest(quantidade = qtd))
}
