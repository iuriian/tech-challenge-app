package br.com.fiap.oficina.api.controller

import br.com.fiap.oficina.api.support.ApiFixtures
import br.com.fiap.oficina.api.support.ApiTestSupport
import br.com.fiap.oficina.peca.application.dto.EstoquePecaRequest
import br.com.fiap.oficina.peca.application.dto.PecaRequest
import br.com.fiap.oficina.peca.application.dto.StatusPecaRequest
import br.com.fiap.oficina.peca.application.usecase.AtualizarPecaUseCase
import br.com.fiap.oficina.peca.application.usecase.AtualizarStatusPecaUseCase
import br.com.fiap.oficina.peca.application.usecase.BuscarPecaPorCodigoUseCase
import br.com.fiap.oficina.peca.application.usecase.CadastrarPecaUseCase
import br.com.fiap.oficina.peca.application.usecase.EstoquePecasUseCase
import br.com.fiap.oficina.peca.application.usecase.ListarPecasUseCase
import br.com.fiap.oficina.shared.domain.exception.ConflictException
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

@DisplayName("Controller - Peca")
class PecaControllerTest {
    private val cadastrarPecaUseCase = mockk<CadastrarPecaUseCase>()
    private val atualizarPecaUseCase = mockk<AtualizarPecaUseCase>()
    private val listarPecasUseCase = mockk<ListarPecasUseCase>()
    private val buscarPecaPorCodigoUseCase = mockk<BuscarPecaPorCodigoUseCase>()
    private val atualizarStatusPecaUseCase = mockk<AtualizarStatusPecaUseCase>()
    private val estoquePecasUseCase = mockk<EstoquePecasUseCase>()

    private val mockMvc =
        ApiTestSupport.mockMvc(
            PecaController(
                cadastrarPecaUseCase = cadastrarPecaUseCase,
                atualizarPecaUseCase = atualizarPecaUseCase,
                listarPecasUseCase = listarPecasUseCase,
                buscarPecaPorCodigoUseCase = buscarPecaPorCodigoUseCase,
                atualizarStatusPecaUseCase = atualizarStatusPecaUseCase,
                estoquePecasUseCase = estoquePecasUseCase,
            ),
        )

    @Test
    @DisplayName("Dado uma peca valida, quando cadastrar, entao deve retornar 201 com o corpo criado")
    fun givenValidPeca_whenCadastrar_thenReturn201WithBody() {
        val capturado = slot<PecaRequest>()
        every { cadastrarPecaUseCase.executar(capture(capturado)) } returns ApiFixtures.pecaResponse()

        mockMvc
            .perform(
                post("/api/v2/pecas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(ApiTestSupport.json(ApiFixtures.pecaRequest())),
            ).andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(ApiFixtures.PECA_ID))
            .andExpect(jsonPath("$.codigo").value(ApiFixtures.CODIGO_PECA))
            .andExpect(jsonPath("$.qtdEstoque").value(20))
            .andExpect(jsonPath("$.ativo").value(true))

        assertEquals(ApiFixtures.CODIGO_PECA, capturado.captured.codigo)
    }

    @Test
    @DisplayName("Dado um codigo menor que o minimo, quando cadastrar, entao deve retornar 400 detalhando o campo")
    fun givenCodigoBelowMinimumSize_whenCadastrar_thenReturn400WithFieldDetails() {
        mockMvc
            .perform(
                post("/api/v2/pecas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(ApiTestSupport.json(ApiFixtures.pecaRequest(codigo = "AB"))),
            ).andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.error").value("Dados inválidos"))
            .andExpect(jsonPath("$.fields.codigo").exists())

        verify(exactly = 0) { cadastrarPecaUseCase.executar(any()) }
    }

    @Test
    @DisplayName("Dado um codigo ja cadastrado, quando cadastrar, entao deve retornar 409")
    fun givenDuplicatedCodigo_whenCadastrar_thenReturn409() {
        every { cadastrarPecaUseCase.executar(any()) } throws ConflictException("Peça já cadastrada")

        mockMvc
            .perform(
                post("/api/v2/pecas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(ApiTestSupport.json(ApiFixtures.pecaRequest())),
            ).andExpect(status().isConflict)
            .andExpect(jsonPath("$.message").value("Peça já cadastrada"))
    }

    @Test
    @DisplayName("Dado dados validos, quando atualizar, entao deve retornar 200 e delegar codigo e corpo")
    fun givenValidData_whenAtualizar_thenReturn200AndDelegateCodigoAndBody() {
        val codigo = slot<String>()
        val request = slot<PecaRequest>()
        every { atualizarPecaUseCase.executar(capture(codigo), capture(request)) } returns ApiFixtures.pecaResponse()

        mockMvc
            .perform(
                put("/api/v2/pecas/{codigo}", ApiFixtures.CODIGO_PECA)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(ApiTestSupport.json(ApiFixtures.pecaRequest(nome = "Pastilha traseira"))),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.codigo").value(ApiFixtures.CODIGO_PECA))

        assertEquals(ApiFixtures.CODIGO_PECA, codigo.captured)
        assertEquals("Pastilha traseira", request.captured.nome)
    }

    @Test
    @DisplayName("Dado um codigo existente, quando desativar, entao deve retornar 200 e delegar status inativo")
    fun givenExistingCodigo_whenDesativar_thenReturn200AndDelegateInactiveStatus() {
        val status = slot<StatusPecaRequest>()
        every { atualizarStatusPecaUseCase.executar(ApiFixtures.CODIGO_PECA, capture(status)) } returns
            ApiFixtures.pecaResponse(ativo = false)

        mockMvc
            .perform(delete("/api/v2/pecas/{codigo}", ApiFixtures.CODIGO_PECA))
            .andExpect(status().isOk)

        assertEquals(false, status.captured.ativo)
    }

    @Test
    @DisplayName("Dado um codigo existente, quando reativar, entao deve retornar 200 e delegar status ativo")
    fun givenExistingCodigo_whenReativar_thenReturn200AndDelegateActiveStatus() {
        val status = slot<StatusPecaRequest>()
        every { atualizarStatusPecaUseCase.executar(ApiFixtures.CODIGO_PECA, capture(status)) } returns
            ApiFixtures.pecaResponse(ativo = true)

        mockMvc
            .perform(patch("/api/v2/pecas/{codigo}/reativar", ApiFixtures.CODIGO_PECA))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.ativo").value(true))

        assertEquals(true, status.captured.ativo)
    }

    @Test
    @DisplayName("Dado pecas cadastradas, quando listar, entao deve retornar 200 com a colecao")
    fun givenRegisteredPecas_whenListar_thenReturn200WithCollection() {
        every { listarPecasUseCase.executar() } returns
            listOf(ApiFixtures.pecaResponse(), ApiFixtures.pecaResponse(id = "outro-id", codigo = "PEC002"))

        mockMvc
            .perform(get("/api/v2/pecas"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[1].codigo").value("PEC002"))

        verify(exactly = 1) { listarPecasUseCase.executar() }
    }

    @Test
    @DisplayName("Dado um codigo existente, quando buscar por codigo, entao deve retornar 200")
    fun givenExistingCodigo_whenBuscarPorCodigo_thenReturn200() {
        every { buscarPecaPorCodigoUseCase.executar(ApiFixtures.CODIGO_PECA) } returns ApiFixtures.pecaResponse()

        mockMvc
            .perform(get("/api/v2/pecas/codigo/{codigo}", ApiFixtures.CODIGO_PECA))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.codigo").value(ApiFixtures.CODIGO_PECA))

        verify(exactly = 1) { buscarPecaPorCodigoUseCase.executar(ApiFixtures.CODIGO_PECA) }
    }

    @Test
    @DisplayName("Dado um codigo inexistente, quando buscar por codigo, entao deve retornar 404")
    fun givenUnknownCodigo_whenBuscarPorCodigo_thenReturn404() {
        every { buscarPecaPorCodigoUseCase.executar(any()) } throws EntityNotFoundException("Peça não encontrada")

        mockMvc
            .perform(get("/api/v2/pecas/codigo/{codigo}", "PEC999"))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").value("Peça não encontrada"))
    }

    @Test
    @DisplayName("Dado uma quantidade positiva, quando repor pecas, entao deve delegar a quantidade somada")
    fun givenPositiveQuantity_whenReporPecas_thenDelegatePositiveQuantity() {
        val request = slot<EstoquePecaRequest>()
        every { estoquePecasUseCase.executar(ApiFixtures.CODIGO_PECA, capture(request)) } returns
            ApiFixtures.pecaResponse(qtdEstoque = 30)

        mockMvc
            .perform(patch("/api/v2/pecas/{codigo}/estoque/repor", ApiFixtures.CODIGO_PECA).param("qtd", "10"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.qtdEstoque").value(30))

        assertEquals(10, request.captured.quantidade)
    }

    @Test
    @DisplayName("Dado uma quantidade positiva, quando retirar pecas, entao deve delegar a quantidade negativa")
    fun givenPositiveQuantity_whenRetirarPecas_thenDelegateNegativeQuantity() {
        val request = slot<EstoquePecaRequest>()
        every { estoquePecasUseCase.executar(ApiFixtures.CODIGO_PECA, capture(request)) } returns
            ApiFixtures.pecaResponse(qtdEstoque = 15)

        mockMvc
            .perform(patch("/api/v2/pecas/{codigo}/estoque/retirar", ApiFixtures.CODIGO_PECA).param("qtd", "5"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.qtdEstoque").value(15))

        assertEquals(-5, request.captured.quantidade)
    }

    @Test
    @DisplayName("Dado um estoque insuficiente, quando retirar pecas, entao deve retornar 400")
    fun givenInsufficientStock_whenRetirarPecas_thenReturn400() {
        every { estoquePecasUseCase.executar(any(), any()) } throws
            IllegalArgumentException("Quantidade em estoque insuficiente")

        mockMvc
            .perform(patch("/api/v2/pecas/{codigo}/estoque/retirar", ApiFixtures.CODIGO_PECA).param("qtd", "500"))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.error").value("Dados inválidos"))
            .andExpect(jsonPath("$.message").value("Quantidade em estoque insuficiente"))
    }

    @Test
    @DisplayName("Dado uma quantidade nao numerica, quando retirar pecas, entao deve retornar 400")
    fun givenNonNumericQuantity_whenRetirarPecas_thenReturn400() {
        mockMvc
            .perform(patch("/api/v2/pecas/{codigo}/estoque/retirar", ApiFixtures.CODIGO_PECA).param("qtd", "abc"))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.error").value("Parâmetro inválido"))
            .andExpect(jsonPath("$.message").value("Valor inválido para 'qtd'"))

        verify(exactly = 0) { estoquePecasUseCase.executar(any(), any()) }
    }
}
