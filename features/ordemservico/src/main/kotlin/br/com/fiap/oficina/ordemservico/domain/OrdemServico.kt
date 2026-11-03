package br.com.fiap.oficina.ordemservico.domain

import java.time.Duration
import java.time.Instant

data class OrdemServico(
    val id: OrdemServicoId,
    val osNumber: NumeroOrdemServico,
    val descricao: String,
    val status: OrdemServicoStatus = OrdemServicoStatus.RECEBIDA,
    val funcionarioRef: FuncionarioRef,
    val clienteRef: ClienteRef,
    val veiculoRef: VeiculoRef,
    val orcamento: Orcamento = Orcamento(),
    val prazo: Duration? = null,
    val dataAbertura: Instant,
    val dataInicioExecucao: Instant? = null,
    val dataFinalizacao: Instant? = null,
) {
    init {
        require(descricao.isNotBlank()) {
            "Descrição da ordem de serviço é obrigatória"
        }

        require(prazo == null || prazo.toMinutes() > 0) {
            "Prazo da ordem de serviço deve ser maior que zero"
        }
    }

    companion object {
        fun criar(
            osNumber: NumeroOrdemServico,
            descricao: String,
            funcionarioRef: FuncionarioRef,
            clienteRef: ClienteRef,
            veiculoRef: VeiculoRef,
            status: OrdemServicoStatus = OrdemServicoStatus.RECEBIDA,
            orcamento: Orcamento = Orcamento(),
            prazo: Duration? = null,
            dataAbertura: Instant = Instant.now(),
        ): OrdemServico = OrdemServico(
            id = OrdemServicoId.generate(),
            osNumber = osNumber,
            descricao = descricao,
            status = status,
            funcionarioRef = funcionarioRef,
            clienteRef = clienteRef,
            veiculoRef = veiculoRef,
            orcamento = orcamento,
            prazo = prazo,
            dataAbertura = dataAbertura,
        )

        /**
         * Status em que a ordem de serviço ainda admite alteração dos seus dados.
         *
         * A partir de `AGUARDANDO_APROVACAO` o orçamento é a base da aprovação do
         * cliente, e alterá-lo invalidaria silenciosamente o valor aprovado.
         */
        private val STATUS_EDITAVEIS = setOf(OrdemServicoStatus.RECEBIDA, OrdemServicoStatus.EM_DIAGNOSTICO)
    }

    fun podeSerAtualizada(): Boolean = status in STATUS_EDITAVEIS

    /**
     * Devolve a ordem de serviço com os dados informados.
     *
     * Identidade ([id], [osNumber]), [status] e as datas permanecem inalterados:
     * a progressão de status é responsabilidade de [avancarStatus] e [alterarStatus].
     */
    fun atualizar(
        descricao: String,
        funcionarioRef: FuncionarioRef,
        clienteRef: ClienteRef,
        veiculoRef: VeiculoRef,
        orcamento: Orcamento,
    ): OrdemServico {
        check(podeSerAtualizada()) {
            "Ordem de serviço no status '$status' não pode ser atualizada. " +
                "Alterações são permitidas apenas nos status: $STATUS_EDITAVEIS"
        }

        return copy(
            descricao = descricao,
            funcionarioRef = funcionarioRef,
            clienteRef = clienteRef,
            veiculoRef = veiculoRef,
            orcamento = orcamento,
        )
    }

    fun avancarStatus(agora: Instant = Instant.now()): OrdemServico {
        val proximoStatus =
            status.proximoStatus()
                ?: error(
                    "Ordem de serviço no status '$status' é um estado final e não pode ser alterada.",
                )

        return alterarStatus(proximoStatus, agora)
    }

    fun alterarStatus(novoStatus: OrdemServicoStatus, agora: Instant = Instant.now()): OrdemServico {
        val permitidas = status.transicoesPermitidas()

        check(novoStatus in permitidas) {
            "Transição inválida de '$status' para '$novoStatus'. " +
                "Transições permitidas a partir de '$status': $permitidas"
        }

        return when (novoStatus) {
            OrdemServicoStatus.EM_EXECUCAO -> {
                copy(
                    status = novoStatus,
                    dataInicioExecucao = agora,
                )
            }

            OrdemServicoStatus.FINALIZADA -> {
                copy(
                    status = novoStatus,
                    dataFinalizacao = agora,
                )
            }

            else -> {
                copy(status = novoStatus)
            }
        }
    }

    fun adicionarItemOrcamento(item: ItemOrcamento): OrdemServico = copy(orcamento = orcamento.adicionarItem(item))

    /**
     * Tempo decorrido entre o início da execução e a finalização.
     *
     * Retorna `null` enquanto a ordem de serviço não tiver percorrido as duas
     * transições, caso em que ela não compõe a métrica de tempo médio.
     */
    fun tempoExecucaoEmMinutos(): Long? {
        val inicio = dataInicioExecucao ?: return null
        val fim = dataFinalizacao ?: return null

        return Duration.between(inicio, fim).toMinutes()
    }
}
