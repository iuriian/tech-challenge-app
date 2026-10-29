package br.com.fiap.oficina.api.controller

import br.com.fiap.oficina.api.contract.ClienteApi
import br.com.fiap.oficina.cliente.application.dto.ClienteRequest
import br.com.fiap.oficina.cliente.application.dto.ClienteResponse
import br.com.fiap.oficina.cliente.application.usecase.AtualizarClienteUseCase
import br.com.fiap.oficina.cliente.application.usecase.BuscarClientePorDocumentoUseCase
import br.com.fiap.oficina.cliente.application.usecase.BuscarClientePorIdUseCase
import br.com.fiap.oficina.cliente.application.usecase.BuscarClientePorNomeUseCase
import br.com.fiap.oficina.cliente.application.usecase.CriarClienteUseCase
import br.com.fiap.oficina.cliente.application.usecase.ListarClientesUseCase
import br.com.fiap.oficina.cliente.application.usecase.RemoverClienteUseCase
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
internal class ClienteController(
    private val criarClienteUseCase: CriarClienteUseCase,
    private val listarClientesUseCase: ListarClientesUseCase,
    private val buscarClientePorIdUseCase: BuscarClientePorIdUseCase,
    private val buscarClientePorNomeUseCase: BuscarClientePorNomeUseCase,
    private val buscarClientePorDocumentoUseCase: BuscarClientePorDocumentoUseCase,
    private val atualizarClienteUseCase: AtualizarClienteUseCase,
    private val removerClienteUseCase: RemoverClienteUseCase,
) : ClienteApi {
    override fun cadastrar(request: ClienteRequest): ResponseEntity<ClienteResponse> {
        val response = criarClienteUseCase.executar(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    override fun listarTodos(): List<ClienteResponse> = listarClientesUseCase.executar()

    override fun buscarPorId(id: String): ClienteResponse = buscarClientePorIdUseCase.executar(id)

    override fun buscarPorNome(nome: String): ClienteResponse = buscarClientePorNomeUseCase.executar(nome)

    override fun buscarPorDocumento(documentoNumero: String): ClienteResponse =
        buscarClientePorDocumentoUseCase.executar(documentoNumero)

    override fun alterar(id: String, request: ClienteRequest): ClienteResponse =
        atualizarClienteUseCase.executar(id, request)

    override fun deletar(id: String) = removerClienteUseCase.executar(id)
}
