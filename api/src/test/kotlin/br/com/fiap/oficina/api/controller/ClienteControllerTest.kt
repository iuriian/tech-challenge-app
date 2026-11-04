package br.com.fiap.oficina.api.controller

import br.com.fiap.oficina.api.support.ApiFixtures
import br.com.fiap.oficina.api.support.ApiTestSupport
import br.com.fiap.oficina.cliente.application.dto.ClienteRequest
import br.com.fiap.oficina.cliente.application.usecase.AtualizarClienteUseCase
import br.com.fiap.oficina.cliente.application.usecase.BuscarClientePorDocumentoUseCase
import br.com.fiap.oficina.cliente.application.usecase.BuscarClientePorIdUseCase
import br.com.fiap.oficina.cliente.application.usecase.BuscarClientePorNomeUseCase
import br.com.fiap.oficina.cliente.application.usecase.CriarClienteUseCase
import br.com.fiap.oficina.cliente.application.usecase.ListarClientesUseCase
import br.com.fiap.oficina.cliente.application.usecase.RemoverClienteUseCase
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@DisplayName("Controller - Cliente")
class ClienteControllerTest {
    private val criarClienteUseCase = mockk<CriarClienteUseCase>()
    private val listarClientesUseCase = mockk<ListarClientesUseCase>()
    private val buscarClientePorIdUseCase = mockk<BuscarClientePorIdUseCase>()
    private val buscarClientePorNomeUseCase = mockk<BuscarClientePorNomeUseCase>()
    private val buscarClientePorDocumentoUseCase = mockk<BuscarClientePorDocumentoUseCase>()
    private val atualizarClienteUseCase = mockk<AtualizarClienteUseCase>()
    private val removerClienteUseCase = mockk<RemoverClienteUseCase>(relaxUnitFun = true)

    private val mockMvc =
        ApiTestSupport.mockMvc(
            ClienteController(
                criarClienteUseCase = criarClienteUseCase,
                listarClientesUseCase = listarClientesUseCase,
                buscarClientePorIdUseCase = buscarClientePorIdUseCase,
                buscarClientePorNomeUseCase = buscarClientePorNomeUseCase,
                buscarClientePorDocumentoUseCase = buscarClientePorDocumentoUseCase,
                atualizarClienteUseCase = atualizarClienteUseCase,
                removerClienteUseCase = removerClienteUseCase,
            ),
        )

    @Test
    @DisplayName("Dado um cliente valido, quando cadastrar, entao deve retornar 201 com o corpo criado")
    fun givenValidCliente_whenCadastrar_thenReturn201WithBody() {
        val capturado = slot<ClienteRequest>()
        every { criarClienteUseCase.executar(capture(capturado)) } returns ApiFixtures.clienteResponse()

        mockMvc
            .perform(
                post("/api/v2/clientes")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(ApiTestSupport.json(ApiFixtures.clienteRequest())),
            ).andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(ApiFixtures.CLIENTE_ID))
            .andExpect(jsonPath("$.nome").value(ApiFixtures.NOME_CLIENTE))
            .andExpect(jsonPath("$.endereco.cidade").value("Sao Paulo"))
            .andExpect(jsonPath("$.contatos[0].telefone").value("11999998888"))

        assertEquals(ApiFixtures.CPF, capturado.captured.numeroDocumento)
        verify(exactly = 1) { criarClienteUseCase.executar(any()) }
    }

    @Test
    @DisplayName("Dado um documento ja cadastrado, quando cadastrar, entao deve retornar 409")
    fun givenDuplicatedDocumento_whenCadastrar_thenReturn409() {
        every { criarClienteUseCase.executar(any()) } throws ConflictException("Cliente ja cadastrado")

        mockMvc
            .perform(
                post("/api/v2/clientes")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(ApiTestSupport.json(ApiFixtures.clienteRequest())),
            ).andExpect(status().isConflict)
            .andExpect(jsonPath("$.status").value(409))
            .andExpect(jsonPath("$.error").value("Conflito com o estado atual do recurso"))
            .andExpect(jsonPath("$.message").value("Cliente ja cadastrado"))
            .andExpect(jsonPath("$.path").value("/api/v2/clientes"))
    }

    @Test
    @DisplayName("Dado um corpo ilegivel, quando cadastrar, entao deve retornar 400")
    fun givenUnreadableBody_whenCadastrar_thenReturn400() {
        mockMvc
            .perform(
                post("/api/v2/clientes")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{"),
            ).andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.error").value("Corpo inválido"))

        verify(exactly = 0) { criarClienteUseCase.executar(any()) }
    }

    @Test
    @DisplayName("Dado clientes cadastrados, quando listar todos, entao deve retornar 200 com a colecao")
    fun givenRegisteredClientes_whenListarTodos_thenReturn200WithCollection() {
        every { listarClientesUseCase.executar() } returns
            listOf(ApiFixtures.clienteResponse(), ApiFixtures.clienteResponse(id = "outro-id", nome = "Maria"))

        mockMvc
            .perform(get("/api/v2/clientes"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[1].nome").value("Maria"))

        verify(exactly = 1) { listarClientesUseCase.executar() }
    }

    @Test
    @DisplayName("Dado um id existente, quando buscar por id, entao deve retornar 200")
    fun givenExistingId_whenBuscarPorId_thenReturn200() {
        every { buscarClientePorIdUseCase.executar(ApiFixtures.CLIENTE_ID) } returns ApiFixtures.clienteResponse()

        mockMvc
            .perform(get("/api/v2/clientes/{id}", ApiFixtures.CLIENTE_ID))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(ApiFixtures.CLIENTE_ID))

        verify(exactly = 1) { buscarClientePorIdUseCase.executar(ApiFixtures.CLIENTE_ID) }
    }

    @Test
    @DisplayName("Dado um id inexistente, quando buscar por id, entao deve retornar 404")
    fun givenUnknownId_whenBuscarPorId_thenReturn404() {
        every { buscarClientePorIdUseCase.executar(any()) } throws EntityNotFoundException("Cliente nao encontrado")

        mockMvc
            .perform(get("/api/v2/clientes/{id}", "inexistente"))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.error").value("Recurso não encontrado"))
            .andExpect(jsonPath("$.message").value("Cliente nao encontrado"))
    }

    @Test
    @DisplayName("Dado um nome existente, quando buscar por nome, entao deve retornar 200")
    fun givenExistingNome_whenBuscarPorNome_thenReturn200() {
        every { buscarClientePorNomeUseCase.executar(ApiFixtures.NOME_CLIENTE) } returns ApiFixtures.clienteResponse()

        mockMvc
            .perform(get("/api/v2/clientes/nome/{nome}", ApiFixtures.NOME_CLIENTE))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.nome").value(ApiFixtures.NOME_CLIENTE))

        verify(exactly = 1) { buscarClientePorNomeUseCase.executar(ApiFixtures.NOME_CLIENTE) }
    }

    @Test
    @DisplayName("Dado um documento existente, quando buscar por documento, entao deve retornar 200")
    fun givenExistingDocumento_whenBuscarPorDocumento_thenReturn200() {
        every { buscarClientePorDocumentoUseCase.executar(ApiFixtures.CPF) } returns ApiFixtures.clienteResponse()

        mockMvc
            .perform(get("/api/v2/clientes/documento/{documentoNumero}", ApiFixtures.CPF))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.numeroDocumento").value("012.345.678-90"))

        verify(exactly = 1) { buscarClientePorDocumentoUseCase.executar(ApiFixtures.CPF) }
    }

    @Test
    @DisplayName("Dado dados validos, quando alterar, entao deve retornar 200 e delegar id e corpo")
    fun givenValidData_whenAlterar_thenReturn200AndDelegateIdAndBody() {
        val id = slot<String>()
        val request = slot<ClienteRequest>()
        every { atualizarClienteUseCase.executar(capture(id), capture(request)) } returns
            ApiFixtures.clienteResponse(nome = "Joao Alterado")

        mockMvc
            .perform(
                put("/api/v2/clientes/{id}", ApiFixtures.CLIENTE_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(ApiTestSupport.json(ApiFixtures.clienteRequest(nome = "Joao Alterado"))),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.nome").value("Joao Alterado"))

        assertEquals(ApiFixtures.CLIENTE_ID, id.captured)
        assertEquals("Joao Alterado", request.captured.nome)
    }

    @Test
    @DisplayName("Dado um id existente, quando deletar, entao deve retornar 200 e delegar ao use case")
    fun givenExistingId_whenDeletar_thenReturn200AndDelegate() {
        mockMvc
            .perform(delete("/api/v2/clientes/{id}", ApiFixtures.CLIENTE_ID))
            .andExpect(status().isOk)

        verify(exactly = 1) { removerClienteUseCase.executar(ApiFixtures.CLIENTE_ID) }
    }
}
