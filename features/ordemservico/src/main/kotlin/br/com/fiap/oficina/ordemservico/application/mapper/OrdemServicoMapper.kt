package br.com.fiap.oficina.ordemservico.application.mapper

import br.com.fiap.oficina.ordemservico.application.dto.OrdemServicoResponse
import br.com.fiap.oficina.ordemservico.domain.OrdemServico
import org.springframework.stereotype.Component

/**
 * Converte a ordem de serviço para o contrato de saída.
 *
 * Não há conversão inversa (`toDomain`): montar uma [OrdemServico] exige dados
 * resolvidos em outros módulos (cliente, funcionário, veículo, peças e serviços)
 * e a numeração sequencial, orquestração que pertence ao caso de uso.
 */
@Component
internal class OrdemServicoMapper(private val itemOrcamentoMapper: ItemOrcamentoMapper) {
    fun toResponse(domain: OrdemServico): OrdemServicoResponse = OrdemServicoResponse(
        id = domain.id.toString(),
        descricao = domain.descricao,
        status = domain.status,
        funcionarioId = domain.funcionarioRef.toString(),
        clienteId = domain.clienteRef.toString(),
        veiculoId = domain.veiculoRef.toString(),
        itens = domain.orcamento.itens.map(itemOrcamentoMapper::toResponse),
        dataAbertura = domain.dataAbertura,
        dataInicioExecucao = domain.dataInicioExecucao,
        dataFinalizacao = domain.dataFinalizacao,
    )
}
