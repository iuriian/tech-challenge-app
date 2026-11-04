package br.com.fiap.oficina.api.controller

import br.com.fiap.oficina.api.observability.OrdemServicoTelemetry
import br.com.fiap.oficina.api.support.ApiFixtures
import br.com.fiap.oficina.api.support.ApiTestSupport
import br.com.fiap.oficina.ordemservico.application.dto.AlterarStatusRequest
import br.com.fiap.oficina.ordemservico.application.dto.OrdemServicoRequest
import br.com.fiap.oficina.ordemservico.application.usecase.AlterarStatusOrdemServicoUseCase
import br.com.fiap.oficina.ordemservico.application.usecase.AtualizarOrdemServicoUseCase
import br.com.fiap.oficina.ordemservico.application.usecase.AvancarStatusOrdemServicoUseCase
import br.com.fiap.oficina.ordemservico.application.usecase.BuscarOrdemServicoPorClienteUseCase
import br.com.fiap.oficina.ordemservico.application.usecase.BuscarOrdemServicoPorIdUseCase
import br.com.fiap.oficina.ordemservico.application.usecase.CalcularTempoMedioExecucaoUseCase
import br.com.fiap.oficina.ordemservico.application.usecase.CriarOrdemServicoUseCase
import br.com.fiap.oficina.ordemservico.application.usecase.DeletarOrdemServicoUseCase
import br.com.fiap.oficina.ordemservico.application.usecase.ListarOrdemServicoUseCase
import br.com.fiap.oficina.ordemservico.application.usecase.ObterOrcamentoUseCase
import br.com.fiap.oficina.ordemservico.domain.OrdemServicoStatus
import br.com.fiap.oficina.shared.domain.exception.BusinessRuleException
import br.com.fiap.oficina.shared.domain.exception.EntityNotFoundException
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@DisplayName("Controller - Ordem de servico")
class OrdemServicoControllerTest {
    private val criarOrdemServicoUseCase = mockk<CriarOrdemServicoUseCase>()
    private val listarOrdemServicoUseCase = mockk<ListarOrdemServicoUseCase>()
    private val buscarOrdemServicoPorIdUseCase = mockk<BuscarOrdemServicoPorIdUseCase>()
    private val buscarOrdemServicoPorClienteUseCase = mockk<BuscarOrdemServicoPorClienteUseCase>()
    private val obterOrcamentoUseCase = mockk<ObterOrcamentoUseCase>()
    private val calcularTempoMedioExecucaoUseCase = mockk<CalcularTempoMedioExecucaoUseCase>()
    private val atualizarOrdemServicoUseCase = mockk<AtualizarOrdemServicoUseCase>()
    private val avancarStatusOrdemServicoUseCase = mockk<AvancarStatusOrdemServicoUseCase>()
    private val alterarStatusOrdemServicoUseCase = mockk<AlterarStatusOrdemServicoUseCase>()
    private val deletarOrdemServicoUseCase = mockk<DeletarOrdemServicoUseCase>(relaxUnitFun = true)
    private val telemetry = mockk<OrdemServicoTelemetry>(relaxUnitFun = true)

    private val mockMvc =
        ApiTestSupport.mockMvc(
            OrdemServicoController(
                criarOrdemServicoUseCase = criarOrdemServicoUseCase,
                listarOrdemServicoUseCase = listarOrdemServicoUseCase,
                buscarOrdemServicoPorIdUseCase = buscarOrdemServicoPorIdUseCase,
                buscarOrdemServicoPorClienteUseCase = buscarOrdemServicoPorClienteUseCase,
                obterOrcamentoUseCase = obterOrcamentoUseCase,
                calcularTempoMedioExecucaoUseCase = calcularTempoMedioExecucaoUseCase,
                atualizarOrdemServicoUseCase = atualizarOrdemServicoUseCase,
                avancarStatusOrdemServicoUseCase = avancarStatusOrdemServicoUseCase,
                alterarStatusOrdemServicoUseCase = alterarStatusOrdemServicoUseCase,
                deletarOrdemServicoUseCase = deletarOrdemServicoUseCase,
                telemetry = telemetry,
            ),
        )

    @Test
    @DisplayName("Dado uma ordem valida, quando criar, entao deve retornar 201 com o corpo criado")
    fun givenValidOrdem_whenCriar_thenReturn201WithBody() {
        val capturado = slot<OrdemServicoRequest>()
        every { criarOrdemServicoUseCase.executar(capture(capturado)) } returns ApiFixtures.ordemServicoResponse()

        mockMvc
            .perform(
                post("/api/v2/servicos")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(ApiTestSupport.json(ApiFixtures.ordemServicoRequest())),
            ).andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(ApiFixtures.ORDEM_SERVICO_ID))
            .andExpect(jsonPath("$.status").value(OrdemServicoStatus.RECEBIDA.name))
            .andExpect(jsonPath("$.itens.length()").value(1))

        assertEquals(ApiFixtures.CLIENTE_ID, capturado.captured.clienteId)
        assertEquals(ApiFixtures.VEICULO_ID, capturado.captured.veiculoId)
    }

    @Test
    @DisplayName("Dado uma descricao em branco, quando criar, entao deve retornar 400 detalhando o campo")
    fun givenBlankDescricao_whenCriar_thenReturn400WithFieldDetails() {
        mockMvc
            .perform(
                post("/api/v2/servicos")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(ApiTestSupport.json(ApiFixtures.ordemServicoRequest(descricao = ""))),
            ).andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.error").value("Dados inválidos"))
            .andExpect(jsonPath("$.fields.descricao").exists())

        verify(exactly = 0) { criarOrdemServicoUseCase.executar(any()) }
    }

    @Test
    @DisplayName("Dado um corpo ilegivel, quando criar, entao deve retornar 400")
    fun givenUnreadableBody_whenCriar_thenReturn400() {
        mockMvc
            .perform(
                post("/api/v2/servicos")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{"),
            ).andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.error").value("Corpo inválido"))

        verify(exactly = 0) { criarOrdemServicoUseCase.executar(any()) }
    }

    @Test
    @DisplayName("Dado ordens cadastradas, quando listar todos, entao deve retornar 200 com a colecao")
    fun givenRegisteredOrdens_whenListarTodos_thenReturn200WithCollection() {
        every { listarOrdemServicoUseCase.executar() } returns
            listOf(ApiFixtures.ordemServicoResponse(), ApiFixtures.ordemServicoResponse(id = "outro-id"))

        mockMvc
            .perform(get("/api/v2/servicos"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))

        verify(exactly = 1) { listarOrdemServicoUseCase.executar() }
    }

    @Test
    @DisplayName("Dado um id existente, quando buscar por id, entao deve retornar 200")
    fun givenExistingId_whenBuscarPorId_thenReturn200() {
        every { buscarOrdemServicoPorIdUseCase.executar(ApiFixtures.ORDEM_SERVICO_ID) } returns
            ApiFixtures.ordemServicoResponse()

        mockMvc
            .perform(get("/api/v2/servicos/{id}", ApiFixtures.ORDEM_SERVICO_ID))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(ApiFixtures.ORDEM_SERVICO_ID))
            .andExpect(jsonPath("$.descricao").value(ApiFixtures.DESCRICAO_ORDEM_SERVICO))

        verify(exactly = 1) { buscarOrdemServicoPorIdUseCase.executar(ApiFixtures.ORDEM_SERVICO_ID) }
    }

    @Test
    @DisplayName("Dado um id inexistente, quando buscar por id, entao deve retornar 404")
    fun givenUnknownId_whenBuscarPorId_thenReturn404() {
        every { buscarOrdemServicoPorIdUseCase.executar(any()) } throws
            EntityNotFoundException("Ordem de serviço não encontrada")

        mockMvc
            .perform(get("/api/v2/servicos/{id}", ApiFixtures.ORDEM_SERVICO_ID))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").value("Ordem de serviço não encontrada"))
    }

    @Test
    @DisplayName("Dado um cliente com ordens, quando listar por cliente, entao deve retornar 200")
    fun givenClienteWithOrdens_whenListarPorCliente_thenReturn200() {
        every { buscarOrdemServicoPorClienteUseCase.executar(ApiFixtures.CLIENTE_ID) } returns
            listOf(ApiFixtures.ordemServicoResponse())

        mockMvc
            .perform(get("/api/v2/servicos/cliente/{clienteId}", ApiFixtures.CLIENTE_ID))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].clienteId").value(ApiFixtures.CLIENTE_ID))

        verify(exactly = 1) { buscarOrdemServicoPorClienteUseCase.executar(ApiFixtures.CLIENTE_ID) }
    }

    @Test
    @DisplayName("Dado uma ordem com orcamento, quando obter orcamento, entao deve retornar 200 com o total")
    fun givenOrdemWithOrcamento_whenObterOrcamento_thenReturn200WithTotal() {
        every { obterOrcamentoUseCase.executar(ApiFixtures.ORDEM_SERVICO_ID) } returns ApiFixtures.orcamentoResponse()

        mockMvc
            .perform(get("/api/v2/servicos/{id}/orcamento", ApiFixtures.ORDEM_SERVICO_ID))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.valorTotal").value(250.00))
            .andExpect(jsonPath("$.itens.length()").value(1))

        verify(exactly = 1) { obterOrcamentoUseCase.executar(ApiFixtures.ORDEM_SERVICO_ID) }
    }

    @Test
    @DisplayName("Dado um id inexistente, quando obter orcamento, entao deve retornar 404")
    fun givenUnknownId_whenObterOrcamento_thenReturn404() {
        every { obterOrcamentoUseCase.executar(any()) } throws
            EntityNotFoundException("Ordem de serviço não encontrada")

        mockMvc
            .perform(get("/api/v2/servicos/{id}/orcamento", ApiFixtures.ORDEM_SERVICO_ID))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").value("Ordem de serviço não encontrada"))
    }

    @Test
    @DisplayName("Dado ordens finalizadas, quando consultar tempo medio, entao deve retornar 200 com as metricas")
    fun givenFinishedOrdens_whenTempoMedioExecucao_thenReturn200WithMetrics() {
        every { calcularTempoMedioExecucaoUseCase.executar() } returns ApiFixtures.tempoMedioExecucaoResponse()

        mockMvc
            .perform(get("/api/v2/servicos/metricas/tempo-medio"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.totalOrdensFinalizadas").value(3))
            .andExpect(jsonPath("$.tempoMedioMinutos").value(125.5))

        verify(exactly = 1) { calcularTempoMedioExecucaoUseCase.executar() }
    }

    @Test
    @DisplayName("Dado dados validos, quando atualizar, entao deve retornar 200 e delegar id e corpo")
    fun givenValidData_whenAtualizar_thenReturn200AndDelegateIdAndBody() {
        val id = slot<String>()
        val request = slot<OrdemServicoRequest>()
        every { atualizarOrdemServicoUseCase.executar(capture(id), capture(request)) } returns
            ApiFixtures.ordemServicoResponse(descricao = "Revisao completa")

        mockMvc
            .perform(
                put("/api/v2/servicos/{id}", ApiFixtures.ORDEM_SERVICO_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(ApiTestSupport.json(ApiFixtures.ordemServicoRequest(descricao = "Revisao completa"))),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.descricao").value("Revisao completa"))

        assertEquals(ApiFixtures.ORDEM_SERVICO_ID, id.captured)
        assertEquals("Revisao completa", request.captured.descricao)
    }

    @Test
    @DisplayName("Dado uma ordem em status intermediario, quando avancar status, entao deve retornar 200")
    fun givenIntermediateStatus_whenAvancarStatus_thenReturn200() {
        every { avancarStatusOrdemServicoUseCase.executar(ApiFixtures.ORDEM_SERVICO_ID) } returns
            ApiFixtures.ordemServicoResponse(status = OrdemServicoStatus.EM_DIAGNOSTICO)

        mockMvc
            .perform(patch("/api/v2/servicos/{id}/avancar", ApiFixtures.ORDEM_SERVICO_ID))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value(OrdemServicoStatus.EM_DIAGNOSTICO.name))

        verify(exactly = 1) { avancarStatusOrdemServicoUseCase.executar(ApiFixtures.ORDEM_SERVICO_ID) }
    }

    @Test
    @DisplayName("Dado uma ordem em status final, quando avancar status, entao deve retornar 422")
    fun givenFinalStatus_whenAvancarStatus_thenReturn422() {
        every { avancarStatusOrdemServicoUseCase.executar(any()) } throws
            BusinessRuleException("Ordem de serviço no status 'ENTREGUE' é um estado final")

        mockMvc
            .perform(patch("/api/v2/servicos/{id}/avancar", ApiFixtures.ORDEM_SERVICO_ID))
            .andExpect(status().isUnprocessableEntity)
            .andExpect(jsonPath("$.error").value("Regra de negócio violada"))
    }

    @Test
    @DisplayName("Dado uma transicao permitida, quando alterar status, entao deve retornar 200 e delegar o corpo")
    fun givenAllowedTransition_whenAlterarStatus_thenReturn200AndDelegateBody() {
        val id = slot<String>()
        val request = slot<AlterarStatusRequest>()
        every { alterarStatusOrdemServicoUseCase.executar(capture(id), capture(request)) } returns
            ApiFixtures.ordemServicoResponse(status = OrdemServicoStatus.EM_EXECUCAO)

        mockMvc
            .perform(
                patch("/api/v2/servicos/{id}/status", ApiFixtures.ORDEM_SERVICO_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        ApiTestSupport.json(
                            ApiFixtures.alterarStatusRequest(status = OrdemServicoStatus.EM_EXECUCAO),
                        ),
                    ),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value(OrdemServicoStatus.EM_EXECUCAO.name))

        assertEquals(ApiFixtures.ORDEM_SERVICO_ID, id.captured)
        assertEquals(OrdemServicoStatus.EM_EXECUCAO, request.captured.status)
    }

    @Test
    @DisplayName("Dado uma transicao invalida, quando alterar status, entao deve retornar 422")
    fun givenForbiddenTransition_whenAlterarStatus_thenReturn422() {
        every { alterarStatusOrdemServicoUseCase.executar(any(), any()) } throws
            BusinessRuleException("Transição inválida de 'RECEBIDA' para 'ENTREGUE'")

        mockMvc
            .perform(
                patch("/api/v2/servicos/{id}/status", ApiFixtures.ORDEM_SERVICO_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        ApiTestSupport.json(ApiFixtures.alterarStatusRequest(status = OrdemServicoStatus.ENTREGUE)),
                    ),
            ).andExpect(status().isUnprocessableEntity)
            .andExpect(jsonPath("$.message").value("Transição inválida de 'RECEBIDA' para 'ENTREGUE'"))
    }

    @Test
    @DisplayName("Dado um status desconhecido, quando alterar status, entao deve retornar 400")
    fun givenUnknownStatus_whenAlterarStatus_thenReturn400() {
        mockMvc
            .perform(
                patch("/api/v2/servicos/{id}/status", ApiFixtures.ORDEM_SERVICO_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"status":"INEXISTENTE"}"""),
            ).andExpect(status().isBadRequest)

        verify(exactly = 0) { alterarStatusOrdemServicoUseCase.executar(any(), any()) }
    }

    @Test
    @DisplayName("Dado um id existente, quando deletar, entao deve retornar 200 e delegar ao use case")
    fun givenExistingId_whenDeletar_thenReturn200AndDelegate() {
        mockMvc
            .perform(delete("/api/v2/servicos/{id}", ApiFixtures.ORDEM_SERVICO_ID))
            .andExpect(status().isOk)

        verify(exactly = 1) { deletarOrdemServicoUseCase.executar(ApiFixtures.ORDEM_SERVICO_ID) }
    }
}
