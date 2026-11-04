package br.com.fiap.oficina.api.controller

import br.com.fiap.oficina.api.support.ApiFixtures
import br.com.fiap.oficina.api.support.ApiTestSupport
import br.com.fiap.oficina.servico.application.dto.ServicoRequest
import br.com.fiap.oficina.servico.application.usecase.AtualizarServicoUseCase
import br.com.fiap.oficina.servico.application.usecase.BuscarServicoUseCase
import br.com.fiap.oficina.servico.application.usecase.CriarServicoUseCase
import br.com.fiap.oficina.servico.application.usecase.DesativarServicoUseCase
import br.com.fiap.oficina.servico.application.usecase.ListarServicosAtivosUseCase
import br.com.fiap.oficina.servico.application.usecase.ListarTodosServicosUseCase
import br.com.fiap.oficina.servico.application.usecase.ReativarServicoUseCase
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
import java.math.BigDecimal

@DisplayName("Controller - Servico")
class ServicoControllerTest {
    private val criarServicoUseCase = mockk<CriarServicoUseCase>()
    private val listarServicosAtivosUseCase = mockk<ListarServicosAtivosUseCase>()
    private val listarTodosServicosUseCase = mockk<ListarTodosServicosUseCase>()
    private val buscarServicoUseCase = mockk<BuscarServicoUseCase>()
    private val atualizarServicoUseCase = mockk<AtualizarServicoUseCase>()
    private val desativarServicoUseCase = mockk<DesativarServicoUseCase>(relaxUnitFun = true)
    private val reativarServicoUseCase = mockk<ReativarServicoUseCase>()

    private val mockMvc =
        ApiTestSupport.mockMvc(
            ServicoController(
                criarServicoUseCase = criarServicoUseCase,
                listarServicosAtivosUseCase = listarServicosAtivosUseCase,
                listarTodosServicosUseCase = listarTodosServicosUseCase,
                buscarServicoUseCase = buscarServicoUseCase,
                atualizarServicoUseCase = atualizarServicoUseCase,
                desativarServicoUseCase = desativarServicoUseCase,
                reativarServicoUseCase = reativarServicoUseCase,
            ),
        )

    @Test
    @DisplayName("Dado um servico valido, quando criar, entao deve retornar 201 com o corpo criado")
    fun givenValidServico_whenCriar_thenReturn201WithBody() {
        val capturado = slot<ServicoRequest>()
        every { criarServicoUseCase.executar(capture(capturado)) } returns ApiFixtures.servicoResponse()

        mockMvc
            .perform(
                post("/api/v2/catalogo/servicos")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(ApiTestSupport.json(ApiFixtures.servicoRequest())),
            ).andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(ApiFixtures.SERVICO_ID))
            .andExpect(jsonPath("$.descricao").value(ApiFixtures.DESCRICAO_SERVICO))
            .andExpect(jsonPath("$.ativo").value(true))

        assertEquals(ApiFixtures.DESCRICAO_SERVICO, capturado.captured.descricao)
    }

    @Test
    @DisplayName("Dado uma descricao em branco, quando criar, entao deve retornar 400 detalhando o campo")
    fun givenBlankDescricao_whenCriar_thenReturn400WithFieldDetails() {
        mockMvc
            .perform(
                post("/api/v2/catalogo/servicos")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(ApiTestSupport.json(ApiFixtures.servicoRequest(descricao = ""))),
            ).andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.error").value("Dados inválidos"))
            .andExpect(jsonPath("$.fields.descricao").exists())

        verify(exactly = 0) { criarServicoUseCase.executar(any()) }
    }

    @Test
    @DisplayName("Dado um valor negativo, quando criar, entao deve retornar 400 detalhando o campo")
    fun givenNegativeValor_whenCriar_thenReturn400WithFieldDetails() {
        mockMvc
            .perform(
                post("/api/v2/catalogo/servicos")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(ApiTestSupport.json(ApiFixtures.servicoRequest(valor = BigDecimal("-1.00")))),
            ).andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.fields.valor").exists())

        verify(exactly = 0) { criarServicoUseCase.executar(any()) }
    }

    @Test
    @DisplayName("Dado servicos ativos, quando listar ativos, entao deve retornar 200 com a colecao")
    fun givenActiveServicos_whenListarAtivos_thenReturn200WithCollection() {
        every { listarServicosAtivosUseCase.executar() } returns listOf(ApiFixtures.servicoResponse())

        mockMvc
            .perform(get("/api/v2/catalogo/servicos"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].ativo").value(true))

        verify(exactly = 1) { listarServicosAtivosUseCase.executar() }
    }

    @Test
    @DisplayName("Dado servicos ativos e inativos, quando listar todos, entao deve retornar 200 com a colecao")
    fun givenActiveAndInactiveServicos_whenListarTodos_thenReturn200WithCollection() {
        every { listarTodosServicosUseCase.executar() } returns
            listOf(ApiFixtures.servicoResponse(), ApiFixtures.servicoResponse(id = "outro-id", ativo = false))

        mockMvc
            .perform(get("/api/v2/catalogo/servicos/todos"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[1].ativo").value(false))

        verify(exactly = 1) { listarTodosServicosUseCase.executar() }
    }

    @Test
    @DisplayName("Dado um id existente, quando buscar, entao deve retornar 200")
    fun givenExistingId_whenBuscar_thenReturn200() {
        every { buscarServicoUseCase.executar(ApiFixtures.SERVICO_ID) } returns ApiFixtures.servicoResponse()

        mockMvc
            .perform(get("/api/v2/catalogo/servicos/{id}", ApiFixtures.SERVICO_ID))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(ApiFixtures.SERVICO_ID))

        verify(exactly = 1) { buscarServicoUseCase.executar(ApiFixtures.SERVICO_ID) }
    }

    @Test
    @DisplayName("Dado um id inexistente, quando buscar, entao deve retornar 404")
    fun givenUnknownId_whenBuscar_thenReturn404() {
        every { buscarServicoUseCase.executar(any()) } throws EntityNotFoundException("Serviço não encontrado")

        mockMvc
            .perform(get("/api/v2/catalogo/servicos/{id}", ApiFixtures.SERVICO_ID))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").value("Serviço não encontrado"))
    }

    @Test
    @DisplayName("Dado dados validos, quando atualizar, entao deve retornar 200 e delegar id e corpo")
    fun givenValidData_whenAtualizar_thenReturn200AndDelegateIdAndBody() {
        val id = slot<String>()
        val request = slot<ServicoRequest>()
        every { atualizarServicoUseCase.executar(capture(id), capture(request)) } returns
            ApiFixtures.servicoResponse(descricao = "Alinhamento e balanceamento")

        mockMvc
            .perform(
                put("/api/v2/catalogo/servicos/{id}", ApiFixtures.SERVICO_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        ApiTestSupport.json(ApiFixtures.servicoRequest(descricao = "Alinhamento e balanceamento")),
                    ),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.descricao").value("Alinhamento e balanceamento"))

        assertEquals(ApiFixtures.SERVICO_ID, id.captured)
        assertEquals("Alinhamento e balanceamento", request.captured.descricao)
    }

    @Test
    @DisplayName("Dado um id existente, quando desativar, entao deve retornar 200 e delegar ao use case")
    fun givenExistingId_whenDesativar_thenReturn200AndDelegate() {
        mockMvc
            .perform(delete("/api/v2/catalogo/servicos/{id}", ApiFixtures.SERVICO_ID))
            .andExpect(status().isOk)

        verify(exactly = 1) { desativarServicoUseCase.executar(ApiFixtures.SERVICO_ID) }
    }

    @Test
    @DisplayName("Dado um servico desativado, quando reativar, entao deve retornar 200 com o servico ativo")
    fun givenDisabledServico_whenReativar_thenReturn200WithActiveServico() {
        every { reativarServicoUseCase.executar(ApiFixtures.SERVICO_ID) } returns ApiFixtures.servicoResponse()

        mockMvc
            .perform(patch("/api/v2/catalogo/servicos/{id}/reativar", ApiFixtures.SERVICO_ID))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.ativo").value(true))

        verify(exactly = 1) { reativarServicoUseCase.executar(ApiFixtures.SERVICO_ID) }
    }
}
