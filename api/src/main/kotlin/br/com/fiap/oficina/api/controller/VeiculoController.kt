package br.com.fiap.oficina.api.controller

import br.com.fiap.oficina.api.contract.VeiculoApi
import br.com.fiap.oficina.veiculo.application.dto.VeiculoRequest
import br.com.fiap.oficina.veiculo.application.dto.VeiculoResponse
import br.com.fiap.oficina.veiculo.application.usecase.AtualizarVeiculoUseCase
import br.com.fiap.oficina.veiculo.application.usecase.BuscarVeiculoPorIdUseCase
import br.com.fiap.oficina.veiculo.application.usecase.BuscarVeiculoPorPlacaUseCase
import br.com.fiap.oficina.veiculo.application.usecase.BuscarVeiculosPorProprietarioUseCase
import br.com.fiap.oficina.veiculo.application.usecase.CriarVeiculoUseCase
import br.com.fiap.oficina.veiculo.application.usecase.ListarVeiculosUseCase
import br.com.fiap.oficina.veiculo.application.usecase.RemoverVeiculoUseCase
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
internal class VeiculoController(
    private val criarVeiculoUseCase: CriarVeiculoUseCase,
    private val buscarVeiculoPorIdUseCase: BuscarVeiculoPorIdUseCase,
    private val buscarVeiculoPorPlacaUseCase: BuscarVeiculoPorPlacaUseCase,
    private val buscarVeiculosPorProprietarioUseCase: BuscarVeiculosPorProprietarioUseCase,
    private val listarVeiculosUseCase: ListarVeiculosUseCase,
    private val atualizarVeiculoUseCase: AtualizarVeiculoUseCase,
    private val removerVeiculoUseCase: RemoverVeiculoUseCase,
) : VeiculoApi {
    override fun criar(request: VeiculoRequest): ResponseEntity<VeiculoResponse> {
        val response = criarVeiculoUseCase.executar(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    override fun buscarVeiculoPorId(id: String): VeiculoResponse = buscarVeiculoPorIdUseCase.executar(id)

    override fun buscarVeiculoPorPlaca(placa: String): VeiculoResponse = buscarVeiculoPorPlacaUseCase.executar(placa)

    override fun buscarVeiculosPorProprietario(proprietarioId: String): List<VeiculoResponse> =
        buscarVeiculosPorProprietarioUseCase.executar(proprietarioId)

    override fun listarTodos(): List<VeiculoResponse> = listarVeiculosUseCase.executar()

    override fun atualizar(id: String, request: VeiculoRequest): VeiculoResponse =
        atualizarVeiculoUseCase.executar(id, request)

    override fun remover(id: String) = removerVeiculoUseCase.executar(id)
}
