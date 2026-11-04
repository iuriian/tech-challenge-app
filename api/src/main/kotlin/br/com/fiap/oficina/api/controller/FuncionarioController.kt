package br.com.fiap.oficina.api.controller

import br.com.fiap.oficina.api.contract.FuncionarioApi
import br.com.fiap.oficina.funcionario.application.dto.FuncionarioRequest
import br.com.fiap.oficina.funcionario.application.dto.FuncionarioResponse
import br.com.fiap.oficina.funcionario.application.usecase.AtualizarFuncionarioUseCase
import br.com.fiap.oficina.funcionario.application.usecase.BuscarFuncionarioPorIdUseCase
import br.com.fiap.oficina.funcionario.application.usecase.BuscarFuncionarioPorNomeUseCase
import br.com.fiap.oficina.funcionario.application.usecase.CriarFuncionarioUseCase
import br.com.fiap.oficina.funcionario.application.usecase.ListarFuncionariosUseCase
import br.com.fiap.oficina.funcionario.application.usecase.RemoverFuncionarioUseCase
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
internal class FuncionarioController(
    private val criarFuncionarioUseCase: CriarFuncionarioUseCase,
    private val listarFuncionariosUseCase: ListarFuncionariosUseCase,
    private val buscarFuncionarioPorIdUseCase: BuscarFuncionarioPorIdUseCase,
    private val buscarFuncionarioPorNomeUseCase: BuscarFuncionarioPorNomeUseCase,
    private val atualizarFuncionarioUseCase: AtualizarFuncionarioUseCase,
    private val removerFuncionarioUseCase: RemoverFuncionarioUseCase,
) : FuncionarioApi {
    override fun cadastrar(request: FuncionarioRequest): ResponseEntity<FuncionarioResponse> {
        val response = criarFuncionarioUseCase.executar(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    override fun listarTodos(): List<FuncionarioResponse> = listarFuncionariosUseCase.executar()

    override fun buscarPorId(id: String): FuncionarioResponse = buscarFuncionarioPorIdUseCase.executar(id)

    override fun buscarPorNome(nome: String): FuncionarioResponse = buscarFuncionarioPorNomeUseCase.executar(nome)

    override fun alterar(id: String, request: FuncionarioRequest): FuncionarioResponse =
        atualizarFuncionarioUseCase.executar(id, request)

    override fun deletar(id: String) = removerFuncionarioUseCase.executar(id)
}
