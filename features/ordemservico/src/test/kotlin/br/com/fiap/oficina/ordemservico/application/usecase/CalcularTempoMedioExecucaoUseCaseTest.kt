package br.com.fiap.oficina.ordemservico.application.usecase

import br.com.fiap.oficina.ordemservico.OrdemServicoFixtures
import br.com.fiap.oficina.ordemservico.domain.OrdemServicoRepository
import br.com.fiap.oficina.ordemservico.domain.OrdemServicoStatus
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Use case - Calcular tempo médio de execução")
class CalcularTempoMedioExecucaoUseCaseTest {
    private val repositoryMock = mockk<OrdemServicoRepository>()
    private val usecase = CalcularTempoMedioExecucaoUseCaseImpl(repositoryMock)

    @Test
    @DisplayName("Dado ordens finalizadas, quando calcular, então deve retornar a média dos minutos")
    fun givenFinishedOrdens_whenCalculating_thenReturnMinutesAverage() {
        every { repositoryMock.listarTodos() } returns
            listOf(
                OrdemServicoFixtures.ordemServicoFinalizada(),
                OrdemServicoFixtures.ordemServico(
                    status = OrdemServicoStatus.FINALIZADA,
                    dataInicioExecucao = OrdemServicoFixtures.DATA_INICIO_EXECUCAO,
                    dataFinalizacao = OrdemServicoFixtures.DATA_INICIO_EXECUCAO.plusSeconds(1800),
                ),
            )

        val response = usecase.executar()

        assertEquals(2, response.totalOrdensFinalizadas)
        assertEquals(90.0, response.tempoMedioMinutos)

        verify(exactly = 1) { repositoryMock.listarTodos() }
    }

    @Test
    @DisplayName("Dado ordens não finalizadas, quando calcular, então deve ignorá-las no cálculo")
    fun givenUnfinishedOrdens_whenCalculating_thenIgnoreThem() {
        every { repositoryMock.listarTodos() } returns
            listOf(
                OrdemServicoFixtures.ordemServicoFinalizada(),
                OrdemServicoFixtures.ordemServico(),
                OrdemServicoFixtures.ordemServico(
                    status = OrdemServicoStatus.EM_EXECUCAO,
                    dataInicioExecucao = OrdemServicoFixtures.DATA_INICIO_EXECUCAO,
                ),
            )

        val response = usecase.executar()

        assertEquals(1, response.totalOrdensFinalizadas)
        assertEquals(OrdemServicoFixtures.MINUTOS_EXECUCAO.toDouble(), response.tempoMedioMinutos)
    }

    @Test
    @DisplayName("Dado nenhuma ordem finalizada, quando calcular, então deve retornar tempo médio nulo")
    fun givenNoFinishedOrdens_whenCalculating_thenReturnNullAverage() {
        every { repositoryMock.listarTodos() } returns listOf(OrdemServicoFixtures.ordemServico())

        val response = usecase.executar()

        assertEquals(0, response.totalOrdensFinalizadas)
        assertNull(response.tempoMedioMinutos)
    }

    @Test
    @DisplayName("Dado nenhuma ordem cadastrada, quando calcular, então deve retornar métrica vazia")
    fun givenNoRegisteredOrdens_whenCalculating_thenReturnEmptyMetric() {
        every { repositoryMock.listarTodos() } returns emptyList()

        val response = usecase.executar()

        assertEquals(0, response.totalOrdensFinalizadas)
        assertNull(response.tempoMedioMinutos)
    }
}
