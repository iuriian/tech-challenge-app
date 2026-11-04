package br.com.fiap.oficina.ordemservico.application.resolver

import br.com.fiap.oficina.ordemservico.application.dto.OrdemServicoRequest
import br.com.fiap.oficina.ordemservico.domain.ClienteRef
import br.com.fiap.oficina.ordemservico.domain.FuncionarioRef
import br.com.fiap.oficina.ordemservico.domain.VeiculoRef
import br.com.fiap.oficina.ordemservico.domain.port.cliente.ConsultarCliente
import br.com.fiap.oficina.ordemservico.domain.port.funcionario.ConsultarFuncionario
import br.com.fiap.oficina.ordemservico.domain.port.veiculo.ConsultarVeiculo
import br.com.fiap.oficina.shared.domain.exception.BusinessRuleException
import org.springframework.stereotype.Component

/**
 * Resolve as referências de cliente, funcionário e veículo de uma ordem de serviço,
 * garantindo que todas existam nos respectivos contextos.
 */
@Component
internal class ReferenciaResolver(
    private val consultarCliente: ConsultarCliente,
    private val consultarFuncionario: ConsultarFuncionario,
    private val consultarVeiculo: ConsultarVeiculo,
) {
    fun resolver(request: OrdemServicoRequest): ReferenciasResolvidas {
        val clienteRef = ClienteRef.toUUID(request.clienteId)
        val funcionarioRef = FuncionarioRef.toUUID(request.funcionarioId)
        val veiculoRef = VeiculoRef.toUUID(request.veiculoId)

        consultarCliente.buscarPorId(clienteRef)
            ?: throw BusinessRuleException("Cliente não encontrado com o id: ${request.clienteId}")

        consultarFuncionario.buscarPorId(funcionarioRef)
            ?: throw BusinessRuleException("Funcionário não encontrado com o id: ${request.funcionarioId}")

        consultarVeiculo.buscarPorId(veiculoRef)
            ?: throw BusinessRuleException("Veículo não encontrado com o id: ${request.veiculoId}")

        return ReferenciasResolvidas(
            clienteRef = clienteRef,
            funcionarioRef = funcionarioRef,
            veiculoRef = veiculoRef,
        )
    }
}
