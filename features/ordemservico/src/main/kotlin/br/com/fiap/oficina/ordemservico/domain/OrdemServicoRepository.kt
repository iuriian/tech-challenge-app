package br.com.fiap.oficina.ordemservico.domain

import java.time.Instant

interface OrdemServicoRepository {
    fun salvar(ordemServico: OrdemServico): OrdemServico

    fun buscarPorId(id: OrdemServicoId): OrdemServico?

    fun listarTodos(): List<OrdemServico>

    fun buscarPorStatus(status: OrdemServicoStatus): List<OrdemServico>

    fun buscarPorCliente(clienteId: ClienteRef): List<OrdemServico>

    fun buscarPorVeiculo(veiculoId: VeiculoRef): List<OrdemServico>

    fun listarPorDataAberturaEntre(inicio: Instant, fim: Instant): List<OrdemServico>

//    fun existePorId(id: Id): Boolean

    fun deletar(id: OrdemServicoId): Boolean
}
