package br.com.fiap.oficina.ordemservico.domain

import br.com.fiap.oficina.ordemservico.OrdemServicoFixtures
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.ValueSource
import java.time.Duration
import kotlin.test.assertFailsWith

@DisplayName("Domínio - Ordem de Serviço")
class OrdemServicoTest {
    @Test
    @DisplayName("Dado dados válidos, quando criar ordem de serviço, então deve gerar id e nascer recebida")
    fun givenValidData_whenCreatingOrdemServico_thenGenerateIdAndStartAsReceived() {
        val ordemServico =
            OrdemServico.criar(
                osNumber = OrdemServicoFixtures.numero(),
                descricao = OrdemServicoFixtures.DESCRICAO,
                funcionarioRef = FuncionarioRef.toUUID(OrdemServicoFixtures.FUNCIONARIO_ID),
                clienteRef = ClienteRef.toUUID(OrdemServicoFixtures.CLIENTE_ID),
                veiculoRef = VeiculoRef.toUUID(OrdemServicoFixtures.VEICULO_ID),
            )

        assertNotNull(ordemServico.id)
        assertEquals(OrdemServicoStatus.RECEBIDA, ordemServico.status)
        assertEquals(OrdemServicoFixtures.DESCRICAO, ordemServico.descricao)
        assertTrue(ordemServico.orcamento.itens.isEmpty())
        assertNull(ordemServico.prazo)
        assertNull(ordemServico.dataInicioExecucao)
        assertNull(ordemServico.dataFinalizacao)
    }

    @Test
    @DisplayName("Dado duas ordens criadas, quando comparar os ids, então devem ser distintos")
    fun givenTwoCreatedOrdens_whenComparingIds_thenTheyDiffer() {
        val primeira =
            OrdemServico.criar(
                osNumber = OrdemServicoFixtures.numero(),
                descricao = OrdemServicoFixtures.DESCRICAO,
                funcionarioRef = FuncionarioRef.toUUID(OrdemServicoFixtures.FUNCIONARIO_ID),
                clienteRef = ClienteRef.toUUID(OrdemServicoFixtures.CLIENTE_ID),
                veiculoRef = VeiculoRef.toUUID(OrdemServicoFixtures.VEICULO_ID),
            )

        val segunda =
            OrdemServico.criar(
                osNumber = OrdemServicoFixtures.numero(),
                descricao = OrdemServicoFixtures.DESCRICAO,
                funcionarioRef = FuncionarioRef.toUUID(OrdemServicoFixtures.FUNCIONARIO_ID),
                clienteRef = ClienteRef.toUUID(OrdemServicoFixtures.CLIENTE_ID),
                veiculoRef = VeiculoRef.toUUID(OrdemServicoFixtures.VEICULO_ID),
            )

        assertNotEquals(primeira.id, segunda.id)
    }

    @ParameterizedTest(name = "descricao [{0}]")
    @ValueSource(strings = ["", " ", "   "])
    @DisplayName("Dado uma descrição em branco, quando criar ordem de serviço, então deve lançar exceção")
    fun givenBlankDescription_whenCreatingOrdemServico_thenThrowException(descricao: String) {
        val exception =
            assertFailsWith<IllegalArgumentException> {
                OrdemServicoFixtures.ordemServico(descricao = descricao)
            }

        assertEquals("Descrição da ordem de serviço é obrigatória", exception.message)
    }

    @Test
    @DisplayName("Dado um prazo zerado, quando criar ordem de serviço, então deve lançar exceção")
    fun givenZeroDeadline_whenCreatingOrdemServico_thenThrowException() {
        val exception =
            assertFailsWith<IllegalArgumentException> {
                OrdemServicoFixtures.ordemServico(prazo = Duration.ZERO)
            }

        assertEquals("Prazo da ordem de serviço deve ser maior que zero", exception.message)
    }

    @Test
    @DisplayName("Dado um prazo nulo, quando criar ordem de serviço, então deve ser aceito")
    fun givenNullDeadline_whenCreatingOrdemServico_thenAccept() {
        assertNull(OrdemServicoFixtures.ordemServico(prazo = null).prazo)
    }

    @ParameterizedTest(name = "status {0}")
    @EnumSource(value = OrdemServicoStatus::class, names = ["RECEBIDA", "EM_DIAGNOSTICO"])
    @DisplayName("Dado um status editável, quando consultar se pode ser atualizada, então deve permitir")
    fun givenEditableStatus_whenCheckingIfUpdatable_thenAllow(status: OrdemServicoStatus) {
        assertTrue(OrdemServicoFixtures.ordemServico(status = status).podeSerAtualizada())
    }

    @ParameterizedTest(name = "status {0}")
    @EnumSource(
        value = OrdemServicoStatus::class,
        names = ["AGUARDANDO_APROVACAO", "EM_EXECUCAO", "FINALIZADA", "ENTREGUE", "CANCELADA"],
    )
    @DisplayName("Dado um status não editável, quando consultar se pode ser atualizada, então deve recusar")
    fun givenNonEditableStatus_whenCheckingIfUpdatable_thenRefuse(status: OrdemServicoStatus) {
        assertFalse(OrdemServicoFixtures.ordemServico(status = status).podeSerAtualizada())
    }

    @Test
    @DisplayName("Dado uma ordem editável, quando atualizar, então deve alterar os dados e preservar a identidade")
    fun givenEditableOrdem_whenUpdating_thenChangeDataAndKeepIdentity() {
        val original = OrdemServicoFixtures.ordemServico()
        val novoOrcamento = OrdemServicoFixtures.orcamento(itens = listOf(OrdemServicoFixtures.itemServico()))

        val atualizada =
            original.atualizar(
                descricao = OrdemServicoFixtures.DESCRICAO_ALTERNATIVA,
                funcionarioRef = FuncionarioRef.toUUID(OrdemServicoFixtures.ID_ALTERNATIVO),
                clienteRef = ClienteRef.toUUID(OrdemServicoFixtures.ID_ALTERNATIVO),
                veiculoRef = VeiculoRef.toUUID(OrdemServicoFixtures.ID_ALTERNATIVO),
                orcamento = novoOrcamento,
            )

        assertEquals(OrdemServicoFixtures.DESCRICAO_ALTERNATIVA, atualizada.descricao)
        assertEquals(novoOrcamento, atualizada.orcamento)
        assertEquals(OrdemServicoFixtures.ID_ALTERNATIVO, atualizada.clienteRef.toString())
        assertEquals(original.id, atualizada.id)
        assertEquals(original.osNumber, atualizada.osNumber)
        assertEquals(original.status, atualizada.status)
        assertEquals(original.dataAbertura, atualizada.dataAbertura)
    }

    @Test
    @DisplayName("Dado uma ordem em execução, quando atualizar, então deve lançar exceção")
    fun givenOrdemInExecution_whenUpdating_thenThrowException() {
        val ordemServico = OrdemServicoFixtures.ordemServico(status = OrdemServicoStatus.EM_EXECUCAO)

        val exception =
            assertFailsWith<IllegalStateException> {
                ordemServico.atualizar(
                    descricao = OrdemServicoFixtures.DESCRICAO_ALTERNATIVA,
                    funcionarioRef = ordemServico.funcionarioRef,
                    clienteRef = ordemServico.clienteRef,
                    veiculoRef = ordemServico.veiculoRef,
                    orcamento = ordemServico.orcamento,
                )
            }

        assertTrue(exception.message!!.contains("não pode ser atualizada"))
    }

    @Test
    @DisplayName("Dado uma ordem recebida, quando avançar status, então deve ir para em diagnóstico")
    fun givenReceivedOrdem_whenAdvancingStatus_thenMoveToDiagnosis() {
        val avancada = OrdemServicoFixtures.ordemServico().avancarStatus()

        assertEquals(OrdemServicoStatus.EM_DIAGNOSTICO, avancada.status)
        assertNull(avancada.dataInicioExecucao)
        assertNull(avancada.dataFinalizacao)
    }

    @ParameterizedTest(name = "status {0}")
    @EnumSource(value = OrdemServicoStatus::class, names = ["ENTREGUE", "CANCELADA"])
    @DisplayName("Dado uma ordem em status final, quando avançar status, então deve lançar exceção")
    fun givenOrdemInFinalStatus_whenAdvancingStatus_thenThrowException(status: OrdemServicoStatus) {
        val exception =
            assertFailsWith<IllegalStateException> {
                OrdemServicoFixtures.ordemServico(status = status).avancarStatus()
            }

        assertTrue(exception.message!!.contains("estado final"))
    }

    @Test
    @DisplayName("Dado uma ordem aguardando aprovação, quando alterar para em execução, então deve marcar o início")
    fun givenOrdemAwaitingApproval_whenChangingToExecution_thenStampStart() {
        val alterada =
            OrdemServicoFixtures
                .ordemServico(status = OrdemServicoStatus.AGUARDANDO_APROVACAO)
                .alterarStatus(OrdemServicoStatus.EM_EXECUCAO, OrdemServicoFixtures.DATA_INICIO_EXECUCAO)

        assertEquals(OrdemServicoStatus.EM_EXECUCAO, alterada.status)
        assertEquals(OrdemServicoFixtures.DATA_INICIO_EXECUCAO, alterada.dataInicioExecucao)
        assertNull(alterada.dataFinalizacao)
    }

    @Test
    @DisplayName("Dado uma ordem em execução, quando alterar para finalizada, então deve marcar a finalização")
    fun givenOrdemInExecution_whenChangingToFinished_thenStampCompletion() {
        val alterada =
            OrdemServicoFixtures
                .ordemServico(
                    status = OrdemServicoStatus.EM_EXECUCAO,
                    dataInicioExecucao = OrdemServicoFixtures.DATA_INICIO_EXECUCAO,
                ).alterarStatus(OrdemServicoStatus.FINALIZADA, OrdemServicoFixtures.DATA_FINALIZACAO)

        assertEquals(OrdemServicoStatus.FINALIZADA, alterada.status)
        assertEquals(OrdemServicoFixtures.DATA_INICIO_EXECUCAO, alterada.dataInicioExecucao)
        assertEquals(OrdemServicoFixtures.DATA_FINALIZACAO, alterada.dataFinalizacao)
    }

    @Test
    @DisplayName("Dado uma ordem aguardando aprovação, quando cancelar, então não deve marcar datas")
    fun givenOrdemAwaitingApproval_whenCancelling_thenDoNotStampDates() {
        val alterada =
            OrdemServicoFixtures
                .ordemServico(status = OrdemServicoStatus.AGUARDANDO_APROVACAO)
                .alterarStatus(OrdemServicoStatus.CANCELADA)

        assertEquals(OrdemServicoStatus.CANCELADA, alterada.status)
        assertNull(alterada.dataInicioExecucao)
        assertNull(alterada.dataFinalizacao)
    }

    @Test
    @DisplayName("Dado uma transição não permitida, quando alterar status, então deve lançar exceção")
    fun givenForbiddenTransition_whenChangingStatus_thenThrowException() {
        val exception =
            assertFailsWith<IllegalStateException> {
                OrdemServicoFixtures.ordemServico().alterarStatus(OrdemServicoStatus.ENTREGUE)
            }

        assertTrue(exception.message!!.contains("Transição inválida"))
    }

    @Test
    @DisplayName("Dado uma ordem com orçamento, quando adicionar item, então deve somar ao orçamento")
    fun givenOrdemWithBudget_whenAddingItem_thenAppendToBudget() {
        val ordemServico = OrdemServicoFixtures.ordemServico(orcamento = OrdemServicoFixtures.orcamento(emptyList()))

        val comItem = ordemServico.adicionarItemOrcamento(OrdemServicoFixtures.itemPeca())

        assertEquals(1, comItem.orcamento.itens.size)
        assertTrue(ordemServico.orcamento.itens.isEmpty())
    }

    @Test
    @DisplayName("Dado uma ordem finalizada, quando calcular o tempo de execução, então deve retornar os minutos")
    fun givenFinishedOrdem_whenCalculatingExecutionTime_thenReturnMinutes() {
        assertEquals(
            OrdemServicoFixtures.MINUTOS_EXECUCAO,
            OrdemServicoFixtures.ordemServicoFinalizada().tempoExecucaoEmMinutos(),
        )
    }

    @Test
    @DisplayName("Dado uma ordem sem início de execução, quando calcular o tempo, então deve retornar nulo")
    fun givenOrdemWithoutStart_whenCalculatingExecutionTime_thenReturnNull() {
        assertNull(OrdemServicoFixtures.ordemServico().tempoExecucaoEmMinutos())
    }

    @Test
    @DisplayName("Dado uma ordem iniciada e não finalizada, quando calcular o tempo, então deve retornar nulo")
    fun givenStartedAndUnfinishedOrdem_whenCalculatingExecutionTime_thenReturnNull() {
        val ordemServico =
            OrdemServicoFixtures.ordemServico(
                status = OrdemServicoStatus.EM_EXECUCAO,
                dataInicioExecucao = OrdemServicoFixtures.DATA_INICIO_EXECUCAO,
            )

        assertNull(ordemServico.tempoExecucaoEmMinutos())
    }

    @Test
    @DisplayName("Dado os mesmos dados, quando comparar as ordens, então devem ser iguais")
    fun givenSameData_whenComparingOrdens_thenTheyAreEqual() {
        val primeira = OrdemServicoFixtures.ordemServico()
        val segunda = OrdemServicoFixtures.ordemServico()

        assertEquals(primeira, segunda)
        assertEquals(primeira.hashCode(), segunda.hashCode())
        assertEquals(primeira, primeira.copy())
        assertEquals(OrdemServicoFixtures.DESCRICAO, primeira.component3())
        assertTrue(primeira.toString().contains(OrdemServicoFixtures.DESCRICAO))
    }
}
