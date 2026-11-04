package br.com.fiap.oficina.ordemservico.infrastructure.mapper

import br.com.fiap.oficina.ordemservico.domain.ClienteRef
import br.com.fiap.oficina.ordemservico.domain.FuncionarioRef
import br.com.fiap.oficina.ordemservico.domain.NumeroOrdemServico
import br.com.fiap.oficina.ordemservico.domain.Orcamento
import br.com.fiap.oficina.ordemservico.domain.OrdemServico
import br.com.fiap.oficina.ordemservico.domain.OrdemServicoId
import br.com.fiap.oficina.ordemservico.domain.VeiculoRef
import br.com.fiap.oficina.ordemservico.infrastructure.persistence.OrdemServicoJPA
import br.com.fiap.oficina.shared.domain.exception.BusinessRuleException
import br.com.fiap.oficina.shared.domain.mapper.JPAMapper
import java.time.Duration

internal class OrdemServicoJPAMapper(private val itemMapper: ItemOrcamentoJPAMapper) :
    JPAMapper<OrdemServico, OrdemServicoJPA> {
    override fun toDomain(jpa: OrdemServicoJPA): OrdemServico = OrdemServico(
        id = OrdemServicoId(jpa.id),
        osNumber = NumeroOrdemServico(
            jpa.osNumber
                ?: throw BusinessRuleException("Ordem de serviço sem número de negócio, id: ${jpa.id}"),
        ),
        descricao = jpa.descricao,
        status = jpa.status,
        funcionarioRef = FuncionarioRef(jpa.funcionarioId),
        clienteRef = ClienteRef(jpa.clienteId),
        veiculoRef = VeiculoRef(jpa.veiculoId),
        orcamento = Orcamento(itens = jpa.itens.map(itemMapper::toDomain)),
        prazo = jpa.prazoMinutos?.let(Duration::ofMinutes),
        dataAbertura = jpa.dataAbertura,
        dataInicioExecucao = jpa.dataInicioExecucao,
        dataFinalizacao = jpa.dataFinalizacao,
    )

    override fun toJPA(domain: OrdemServico): OrdemServicoJPA = OrdemServicoJPA(
        id = domain.id.value,
        osNumber = domain.osNumber.valor,
        descricao = domain.descricao,
        status = domain.status,
        funcionarioId = domain.funcionarioRef.value,
        clienteId = domain.clienteRef.value,
        veiculoId = domain.veiculoRef.value,
        itens = domain.orcamento.itens.map(itemMapper::toJPA),
        prazoMinutos = domain.prazo?.toMinutes(),
        dataAbertura = domain.dataAbertura,
        dataInicioExecucao = domain.dataInicioExecucao,
        dataFinalizacao = domain.dataFinalizacao,
    )
}
