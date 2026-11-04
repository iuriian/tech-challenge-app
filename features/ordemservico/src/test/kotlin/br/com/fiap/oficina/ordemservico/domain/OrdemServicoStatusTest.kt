package br.com.fiap.oficina.ordemservico.domain

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.EnumSource

@DisplayName("Domínio - Status da Ordem de Serviço")
class OrdemServicoStatusTest {
    @ParameterizedTest(name = "{0} -> {1}")
    @CsvSource(
        "RECEBIDA, EM_DIAGNOSTICO",
        "EM_DIAGNOSTICO, AGUARDANDO_APROVACAO",
        "AGUARDANDO_APROVACAO, EM_EXECUCAO",
        "EM_EXECUCAO, FINALIZADA",
        "FINALIZADA, ENTREGUE",
    )
    @DisplayName("Dado um status intermediário, quando consultar o próximo, então deve seguir o fluxo")
    fun givenIntermediateStatus_whenQueryingNext_thenFollowFlow(
        atual: OrdemServicoStatus,
        esperado: OrdemServicoStatus,
    ) {
        assertEquals(esperado, atual.proximoStatus())
    }

    @ParameterizedTest(name = "status {0}")
    @EnumSource(value = OrdemServicoStatus::class, names = ["ENTREGUE", "CANCELADA"])
    @DisplayName("Dado um status final, quando consultar o próximo, então deve retornar nulo")
    fun givenFinalStatus_whenQueryingNext_thenReturnNull(status: OrdemServicoStatus) {
        assertNull(status.proximoStatus())
    }

    @Test
    @DisplayName("Dado o status aguardando aprovação, quando consultar as transições, então deve permitir cancelamento")
    fun givenAwaitingApproval_whenQueryingTransitions_thenAllowCancellation() {
        val permitidas = OrdemServicoStatus.AGUARDANDO_APROVACAO.transicoesPermitidas()

        assertEquals(
            setOf(OrdemServicoStatus.EM_EXECUCAO, OrdemServicoStatus.CANCELADA),
            permitidas,
        )
    }

    @Test
    @DisplayName("Dado um status intermediário, quando consultar as transições, então deve permitir só o próximo")
    fun givenIntermediateStatus_whenQueryingTransitions_thenAllowOnlyNext() {
        assertEquals(
            setOf(OrdemServicoStatus.EM_DIAGNOSTICO),
            OrdemServicoStatus.RECEBIDA.transicoesPermitidas(),
        )
    }

    @ParameterizedTest(name = "status {0}")
    @EnumSource(value = OrdemServicoStatus::class, names = ["ENTREGUE", "CANCELADA"])
    @DisplayName("Dado um status final, quando consultar as transições, então deve retornar vazio")
    fun givenFinalStatus_whenQueryingTransitions_thenReturnEmpty(status: OrdemServicoStatus) {
        assertTrue(status.transicoesPermitidas().isEmpty())
    }

    @Test
    @DisplayName("Dado os status, quando consultar a descrição, então deve retornar o texto de apresentação")
    fun givenStatuses_whenQueryingDescription_thenReturnPresentationText() {
        assertEquals("Recebida", OrdemServicoStatus.RECEBIDA.descricao)
        assertEquals("Cancelada pelo cliente", OrdemServicoStatus.CANCELADA.descricao)
    }
}
