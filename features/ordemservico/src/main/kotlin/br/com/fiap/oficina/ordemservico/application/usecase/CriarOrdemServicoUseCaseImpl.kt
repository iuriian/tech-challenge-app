package br.com.fiap.oficina.ordemservico.application.usecase

import br.com.fiap.oficina.ordemservico.application.dto.OrdemServicoRequest
import br.com.fiap.oficina.ordemservico.application.dto.OrdemServicoResponse
import br.com.fiap.oficina.ordemservico.application.mapper.OrdemServicoMapper
import br.com.fiap.oficina.ordemservico.application.resolver.OrcamentoResolver
import br.com.fiap.oficina.ordemservico.application.resolver.ReferenciaResolver
import br.com.fiap.oficina.ordemservico.domain.NumeroOrdemServico
import br.com.fiap.oficina.ordemservico.domain.OrdemServico
import br.com.fiap.oficina.ordemservico.domain.OrdemServicoRepository
import br.com.fiap.oficina.ordemservico.domain.SequenciaOrdemServico
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
internal class CriarOrdemServicoUseCaseImpl(
    private val repository: OrdemServicoRepository,
    private val sequencia: SequenciaOrdemServico,
    private val referenciaResolver: ReferenciaResolver,
    private val orcamentoResolver: OrcamentoResolver,
    private val mapper: OrdemServicoMapper,
) : CriarOrdemServicoUseCase {
    @Transactional
    override fun executar(request: OrdemServicoRequest): OrdemServicoResponse {
        val referencias = referenciaResolver.resolver(request)

        val ordemServico =
            OrdemServico.criar(
                osNumber = NumeroOrdemServico.criar(sequencia.proximoValor()),
                descricao = request.descricao,
                funcionarioRef = referencias.funcionarioRef,
                clienteRef = referencias.clienteRef,
                veiculoRef = referencias.veiculoRef,
                orcamento = orcamentoResolver.resolver(request.itens),
            )

        return mapper.toResponse(repository.salvar(ordemServico))
    }
}
