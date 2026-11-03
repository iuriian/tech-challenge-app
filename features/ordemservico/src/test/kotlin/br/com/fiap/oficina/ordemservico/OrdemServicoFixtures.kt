package br.com.fiap.oficina.ordemservico

import br.com.fiap.oficina.cliente.application.port.ClienteQueryDto
import br.com.fiap.oficina.funcionario.application.port.FuncionarioQueryDto
import br.com.fiap.oficina.ordemservico.application.dto.ItemOrcamentoRequest
import br.com.fiap.oficina.ordemservico.application.dto.OrdemServicoRequest
import br.com.fiap.oficina.ordemservico.domain.ClienteRef
import br.com.fiap.oficina.ordemservico.domain.FuncionarioRef
import br.com.fiap.oficina.ordemservico.domain.ItemOrcamento
import br.com.fiap.oficina.ordemservico.domain.ItemReferenciaRef
import br.com.fiap.oficina.ordemservico.domain.NumeroOrdemServico
import br.com.fiap.oficina.ordemservico.domain.Orcamento
import br.com.fiap.oficina.ordemservico.domain.OrdemServico
import br.com.fiap.oficina.ordemservico.domain.OrdemServicoId
import br.com.fiap.oficina.ordemservico.domain.OrdemServicoStatus
import br.com.fiap.oficina.ordemservico.domain.TipoItemOrcamento
import br.com.fiap.oficina.ordemservico.domain.VeiculoRef
import br.com.fiap.oficina.ordemservico.domain.port.cliente.DadosCliente
import br.com.fiap.oficina.ordemservico.domain.port.funcionario.DadosFuncionario
import br.com.fiap.oficina.ordemservico.domain.port.peca.DadosPeca
import br.com.fiap.oficina.ordemservico.domain.port.servico.DadosServico
import br.com.fiap.oficina.ordemservico.domain.port.veiculo.DadosVeiculo
import br.com.fiap.oficina.ordemservico.infrastructure.persistence.ItemOrcamentoJPA
import br.com.fiap.oficina.ordemservico.infrastructure.persistence.OrdemServicoJPA
import br.com.fiap.oficina.peca.application.port.PecaQueryDto
import br.com.fiap.oficina.servico.application.port.ServicoQueryDto
import br.com.fiap.oficina.veiculo.application.port.VeiculoQueryDto
import java.math.BigDecimal
import java.time.Duration
import java.time.Instant
import java.util.UUID

/** Massa de teste compartilhada pelos testes de ordem de serviço. */
internal object OrdemServicoFixtures {
    const val ID = "00000000-0000-0000-0000-000000000001"
    const val ID_ALTERNATIVO = "00000000-0000-0000-0000-000000000002"
    const val CLIENTE_ID = "00000000-0000-0000-0000-0000000000c1"
    const val FUNCIONARIO_ID = "00000000-0000-0000-0000-0000000000f1"
    const val VEICULO_ID = "00000000-0000-0000-0000-0000000000a1"
    const val PECA_ID = "00000000-0000-0000-0000-0000000000e1"
    const val SERVICO_ID = "00000000-0000-0000-0000-0000000000d1"
    const val ID_INVALIDO = "nao-e-um-uuid"

    const val SEQUENCIAL = 42L
    const val DESCRICAO = "Revisão completa"
    const val DESCRICAO_ALTERNATIVA = "Revisão dos freios"
    const val PLACA = "ABC1D23"

    const val CLIENTE_NOME = "Maria Souza"
    const val CLIENTE_DOCUMENTO = "529.982.247-25"
    const val FUNCIONARIO_NOME = "João Mecânico"
    const val FUNCIONARIO_CARGO = "Mecânico"

    const val PECA_CODIGO = "PC-001"
    const val PECA_NOME = "Filtro de óleo"
    const val SERVICO_DESCRICAO = "Troca de óleo"

    /** Minutos entre [DATA_INICIO_EXECUCAO] e [DATA_FINALIZACAO]. */
    const val MINUTOS_EXECUCAO = 150L

    val PECA_PRECO: BigDecimal = BigDecimal("89.90")
    val SERVICO_VALOR: BigDecimal = BigDecimal("150.00")
    val QUANTIDADE_PECA: BigDecimal = BigDecimal("2")
    val QUANTIDADE_SERVICO: BigDecimal = BigDecimal.ONE

    val PRAZO: Duration = Duration.ofHours(4)
    val DATA_ABERTURA: Instant = Instant.parse("2026-01-05T12:00:00Z")
    val DATA_INICIO_EXECUCAO: Instant = Instant.parse("2026-01-06T08:00:00Z")
    val DATA_FINALIZACAO: Instant = Instant.parse("2026-01-06T10:30:00Z")

    fun numero(sequencial: Long = SEQUENCIAL): NumeroOrdemServico = NumeroOrdemServico.criar(sequencial)

    fun dadosCliente(id: String = CLIENTE_ID) = DadosCliente(id = ClienteRef.toUUID(id))

    fun dadosFuncionario(id: String = FUNCIONARIO_ID) = DadosFuncionario(id = FuncionarioRef.toUUID(id))

    fun dadosVeiculo(id: String = VEICULO_ID, placa: String = PLACA) = DadosVeiculo(
        id = VeiculoRef.toUUID(id),
        placa = placa,
    )

    fun dadosPeca(id: String = PECA_ID, ativo: Boolean = true) = DadosPeca(
        id = ItemReferenciaRef.toUUID(id),
        codigo = PECA_CODIGO,
        nome = PECA_NOME,
        precoVenda = PECA_PRECO,
        ativo = ativo,
    )

    fun dadosServico(id: String = SERVICO_ID, ativo: Boolean = true) = DadosServico(
        id = ItemReferenciaRef.toUUID(id),
        descricao = SERVICO_DESCRICAO,
        valor = SERVICO_VALOR,
        ativo = ativo,
    )

    fun itemPeca(quantidade: BigDecimal = QUANTIDADE_PECA) = ItemOrcamento.dePeca(
        peca = dadosPeca(),
        quantidade = quantidade,
    )

    fun itemServico(quantidade: BigDecimal = QUANTIDADE_SERVICO) = ItemOrcamento.deServico(
        servico = dadosServico(),
        quantidade = quantidade,
    )

    fun orcamento(itens: List<ItemOrcamento> = listOf(itemPeca(), itemServico())) = Orcamento.criar(itens)

    @Suppress("LongParameterList")
    fun ordemServico(
        id: String = ID,
        descricao: String = DESCRICAO,
        status: OrdemServicoStatus = OrdemServicoStatus.RECEBIDA,
        orcamento: Orcamento = orcamento(),
        prazo: Duration? = PRAZO,
        dataAbertura: Instant = DATA_ABERTURA,
        dataInicioExecucao: Instant? = null,
        dataFinalizacao: Instant? = null,
    ) = OrdemServico(
        id = OrdemServicoId.toUUID(id),
        osNumber = numero(),
        descricao = descricao,
        status = status,
        funcionarioRef = FuncionarioRef.toUUID(FUNCIONARIO_ID),
        clienteRef = ClienteRef.toUUID(CLIENTE_ID),
        veiculoRef = VeiculoRef.toUUID(VEICULO_ID),
        orcamento = orcamento,
        prazo = prazo,
        dataAbertura = dataAbertura,
        dataInicioExecucao = dataInicioExecucao,
        dataFinalizacao = dataFinalizacao,
    )

    fun ordemServicoFinalizada() = ordemServico(
        status = OrdemServicoStatus.FINALIZADA,
        dataInicioExecucao = DATA_INICIO_EXECUCAO,
        dataFinalizacao = DATA_FINALIZACAO,
    )

    fun itemRequest(
        tipo: TipoItemOrcamento = TipoItemOrcamento.PECA,
        referenciaId: String = PECA_ID,
        quantidade: BigDecimal = QUANTIDADE_PECA,
    ) = ItemOrcamentoRequest(
        tipo = tipo,
        referenciaId = referenciaId,
        quantidade = quantidade,
    )

    fun request(
        descricao: String = DESCRICAO,
        funcionarioId: String = FUNCIONARIO_ID,
        clienteId: String = CLIENTE_ID,
        veiculoId: String = VEICULO_ID,
        itens: List<ItemOrcamentoRequest> = listOf(itemRequest()),
    ) = OrdemServicoRequest(
        descricao = descricao,
        funcionarioId = funcionarioId,
        clienteId = clienteId,
        veiculoId = veiculoId,
        itens = itens,
    )

    fun itemOrcamentoJPA(
        tipo: TipoItemOrcamento = TipoItemOrcamento.PECA,
        referenciaId: String = PECA_ID,
        codigoReferencia: String? = PECA_CODIGO,
    ) = ItemOrcamentoJPA(
        tipo = tipo,
        referenciaId = UUID.fromString(referenciaId),
        descricao = PECA_NOME,
        valorUnitario = PECA_PRECO,
        quantidade = QUANTIDADE_PECA,
        codigoReferencia = codigoReferencia,
    )

    @Suppress("LongParameterList")
    fun ordemServicoJPA(
        id: String = ID,
        osNumber: String? = numero().valor,
        status: OrdemServicoStatus = OrdemServicoStatus.RECEBIDA,
        itens: List<ItemOrcamentoJPA> = listOf(itemOrcamentoJPA()),
        prazoMinutos: Long? = PRAZO.toMinutes(),
        dataInicioExecucao: Instant? = null,
        dataFinalizacao: Instant? = null,
    ) = OrdemServicoJPA(
        id = UUID.fromString(id),
        osNumber = osNumber,
        descricao = DESCRICAO,
        status = status,
        funcionarioId = UUID.fromString(FUNCIONARIO_ID),
        clienteId = UUID.fromString(CLIENTE_ID),
        veiculoId = UUID.fromString(VEICULO_ID),
        itens = itens,
        prazoMinutos = prazoMinutos,
        dataAbertura = DATA_ABERTURA,
        dataInicioExecucao = dataInicioExecucao,
        dataFinalizacao = dataFinalizacao,
    )

    fun clienteQueryDto(id: String = CLIENTE_ID) = ClienteQueryDto(
        id = id,
        nome = CLIENTE_NOME,
        numeroDocumento = CLIENTE_DOCUMENTO,
    )

    fun funcionarioQueryDto(id: String = FUNCIONARIO_ID) = FuncionarioQueryDto(
        id = id,
        nome = FUNCIONARIO_NOME,
        cargo = FUNCIONARIO_CARGO,
    )

    fun veiculoQueryDto(id: String = VEICULO_ID) = VeiculoQueryDto(
        id = id,
        placa = PLACA,
        proprietarioId = CLIENTE_ID,
    )

    fun pecaQueryDto(id: String = PECA_ID, ativo: Boolean = true) = PecaQueryDto(
        id = id,
        codigo = PECA_CODIGO,
        nome = PECA_NOME,
        precoVenda = PECA_PRECO,
        ativo = ativo,
    )

    fun servicoQueryDto(id: String = SERVICO_ID, ativo: Boolean = true) = ServicoQueryDto(
        id = id,
        descricao = SERVICO_DESCRICAO,
        valor = SERVICO_VALOR,
        ativo = ativo,
    )
}
