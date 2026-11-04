package br.com.fiap.oficina.api.controller

import br.com.fiap.oficina.api.support.ApiFixtures
import br.com.fiap.oficina.api.support.ApiTestSupport
import br.com.fiap.oficina.funcionario.application.dto.FuncionarioRequest
import br.com.fiap.oficina.funcionario.application.usecase.AtualizarFuncionarioUseCase
import br.com.fiap.oficina.funcionario.application.usecase.BuscarFuncionarioPorIdUseCase
import br.com.fiap.oficina.funcionario.application.usecase.BuscarFuncionarioPorNomeUseCase
import br.com.fiap.oficina.funcionario.application.usecase.CriarFuncionarioUseCase
import br.com.fiap.oficina.funcionario.application.usecase.ListarFuncionariosUseCase
import br.com.fiap.oficina.funcionario.application.usecase.RemoverFuncionarioUseCase
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@DisplayName("Controller - Funcionario")
class FuncionarioControllerTest {
    private val criarFuncionarioUseCase = mockk<CriarFuncionarioUseCase>()
    private val listarFuncionariosUseCase = mockk<ListarFuncionariosUseCase>()
    private val buscarFuncionarioPorIdUseCase = mockk<BuscarFuncionarioPorIdUseCase>()
    private val buscarFuncionarioPorNomeUseCase = mockk<BuscarFuncionarioPorNomeUseCase>()
    private val atualizarFuncionarioUseCase = mockk<AtualizarFuncionarioUseCase>()
    private val removerFuncionarioUseCase = mockk<RemoverFuncionarioUseCase>(relaxUnitFun = true)

    private val mockMvc =
        ApiTestSupport.mockMvc(
            FuncionarioController(
                criarFuncionarioUseCase = criarFuncionarioUseCase,
                listarFuncionariosUseCase = listarFuncionariosUseCase,
                buscarFuncionarioPorIdUseCase = buscarFuncionarioPorIdUseCase,
                buscarFuncionarioPorNomeUseCase = buscarFuncionarioPorNomeUseCase,
                atualizarFuncionarioUseCase = atualizarFuncionarioUseCase,
                removerFuncionarioUseCase = removerFuncionarioUseCase,
            ),
        )

    @Test
    @DisplayName("Dado um funcionario valido, quando cadastrar, entao deve retornar 201 com o corpo criado")
    fun givenValidFuncionario_whenCadastrar_thenReturn201WithBody() {
        val capturado = slot<FuncionarioRequest>()
        every { criarFuncionarioUseCase.executar(capture(capturado)) } returns ApiFixtures.funcionarioResponse()

        mockMvc
            .perform(
                post("/api/v2/funcionarios")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(ApiTestSupport.json(ApiFixtures.funcionarioRequest())),
            ).andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(ApiFixtures.FUNCIONARIO_ID))
            .andExpect(jsonPath("$.cargoDescricao").value("Mecanico"))

        assertEquals("MECANICO", capturado.captured.cargo)
        verify(exactly = 1) { criarFuncionarioUseCase.executar(any()) }
    }

    @Test
    @DisplayName("Dado um corpo com campos em branco, quando cadastrar, entao deve retornar 400 detalhando os campos")
    fun givenBlankFields_whenCadastrar_thenReturn400WithFieldDetails() {
        mockMvc
            .perform(
                post("/api/v2/funcionarios")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(ApiTestSupport.json(ApiFixtures.funcionarioRequest(nome = "  ", cargo = ""))),
            ).andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.error").value("Dados inválidos"))
            .andExpect(jsonPath("$.message").value("Os dados fornecidos são inválidos"))
            .andExpect(jsonPath("$.fields.nome").exists())
            .andExpect(jsonPath("$.fields.cargo").exists())

        verify(exactly = 0) { criarFuncionarioUseCase.executar(any()) }
    }

    @Test
    @DisplayName("Dado um cargo inexistente, quando cadastrar, entao deve retornar 422")
    fun givenUnknownCargo_whenCadastrar_thenReturn422() {
        every { criarFuncionarioUseCase.executar(any()) } throws BusinessRuleException("Cargo inválido")

        mockMvc
            .perform(
                post("/api/v2/funcionarios")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(ApiTestSupport.json(ApiFixtures.funcionarioRequest(cargo = "PILOTO"))),
            ).andExpect(status().isUnprocessableEntity)
            .andExpect(jsonPath("$.status").value(422))
            .andExpect(jsonPath("$.error").value("Regra de negócio violada"))
            .andExpect(jsonPath("$.message").value("Cargo inválido"))
    }

    @Test
    @DisplayName("Dado funcionarios cadastrados, quando listar todos, entao deve retornar 200 com a colecao")
    fun givenRegisteredFuncionarios_whenListarTodos_thenReturn200WithCollection() {
        every { listarFuncionariosUseCase.executar() } returns
            listOf(
                ApiFixtures.funcionarioResponse(),
                ApiFixtures.funcionarioResponse(id = "outro-id", nome = "Rodrygo"),
            )

        mockMvc
            .perform(get("/api/v2/funcionarios"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[1].nome").value("Rodrygo"))

        verify(exactly = 1) { listarFuncionariosUseCase.executar() }
    }

    @Test
    @DisplayName("Dado um id existente, quando buscar por id, entao deve retornar 200")
    fun givenExistingId_whenBuscarPorId_thenReturn200() {
        every { buscarFuncionarioPorIdUseCase.executar(ApiFixtures.FUNCIONARIO_ID) } returns
            ApiFixtures.funcionarioResponse()

        mockMvc
            .perform(get("/api/v2/funcionarios/id/{id}", ApiFixtures.FUNCIONARIO_ID))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(ApiFixtures.FUNCIONARIO_ID))

        verify(exactly = 1) { buscarFuncionarioPorIdUseCase.executar(ApiFixtures.FUNCIONARIO_ID) }
    }

    @Test
    @DisplayName("Dado um nome existente, quando buscar por nome, entao deve retornar 200")
    fun givenExistingNome_whenBuscarPorNome_thenReturn200() {
        every { buscarFuncionarioPorNomeUseCase.executar("Vini Jr.") } returns ApiFixtures.funcionarioResponse()

        mockMvc
            .perform(get("/api/v2/funcionarios/nome/{nome}", "Vini Jr."))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.nome").value("Vini Jr."))

        verify(exactly = 1) { buscarFuncionarioPorNomeUseCase.executar("Vini Jr.") }
    }

    @Test
    @DisplayName("Dado um nome inexistente, quando buscar por nome, entao deve retornar 404")
    fun givenUnknownNome_whenBuscarPorNome_thenReturn404() {
        every { buscarFuncionarioPorNomeUseCase.executar(any()) } throws
            EntityNotFoundException("Funcionário não encontrado")

        mockMvc
            .perform(get("/api/v2/funcionarios/nome/{nome}", "Fulano"))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").value("Funcionário não encontrado"))
    }

    @Test
    @DisplayName("Dado dados validos, quando alterar, entao deve retornar 200 e delegar id e corpo")
    fun givenValidData_whenAlterar_thenReturn200AndDelegateIdAndBody() {
        val id = slot<String>()
        val request = slot<FuncionarioRequest>()
        every { atualizarFuncionarioUseCase.executar(capture(id), capture(request)) } returns
            ApiFixtures.funcionarioResponse(nome = "Vinicius Junior")

        mockMvc
            .perform(
                put("/api/v2/funcionarios/{id}", ApiFixtures.FUNCIONARIO_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(ApiTestSupport.json(ApiFixtures.funcionarioRequest(nome = "Vinicius Junior"))),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.nome").value("Vinicius Junior"))

        assertEquals(ApiFixtures.FUNCIONARIO_ID, id.captured)
        assertEquals("Vinicius Junior", request.captured.nome)
    }

    @Test
    @DisplayName("Dado um id existente, quando deletar, entao deve retornar 200 e delegar ao use case")
    fun givenExistingId_whenDeletar_thenReturn200AndDelegate() {
        mockMvc
            .perform(delete("/api/v2/funcionarios/{id}", ApiFixtures.FUNCIONARIO_ID))
            .andExpect(status().isOk)

        verify(exactly = 1) { removerFuncionarioUseCase.executar(ApiFixtures.FUNCIONARIO_ID) }
    }
}
