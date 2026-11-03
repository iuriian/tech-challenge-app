package br.com.fiap.oficina.ordemservico.application.resolver

import br.com.fiap.oficina.ordemservico.domain.ClienteRef
import br.com.fiap.oficina.ordemservico.domain.FuncionarioRef
import br.com.fiap.oficina.ordemservico.domain.VeiculoRef

/**
 * Referências de outros contextos já validadas como existentes.
 */
internal data class ReferenciasResolvidas(
    val clienteRef: ClienteRef,
    val funcionarioRef: FuncionarioRef,
    val veiculoRef: VeiculoRef,
)
