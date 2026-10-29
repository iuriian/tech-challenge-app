package br.com.fiap.oficina.api.controller

import br.com.fiap.oficina.api.support.ApiFixtures
import br.com.fiap.oficina.api.support.ApiTestSupport
import br.com.fiap.oficina.shared.domain.exception.EntityNotFoundException
import br.com.fiap.oficina.veiculo.application.dto.VeiculoRequest
import br.com.fiap.oficina.veiculo.application.usecase.AtualizarVeiculoUseCase
import br.com.fiap.oficina.veiculo.application.usecase.BuscarVeiculoPorIdUseCase
import br.com.fiap.oficina.veiculo.application.usecase.BuscarVeiculoPorPlacaUseCase
import br.com.fiap.oficina.veiculo.application.usecase.BuscarVeiculosPorProprietarioUseCase
import br.com.fiap.oficina.veiculo.application.usecase.CriarVeiculoUseCase
import br.com.fiap.oficina.veiculo.application.usecase.ListarVeiculosUseCase
import br.com.fiap.oficina.veiculo.application.usecase.RemoverVeiculoUseCase
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

@DisplayName("Controller - Veiculo")
class VeiculoControllerTest {
    private val criarVeiculoUseCase = mockk<CriarVeiculoUseCase>()
    private val buscarVeiculoPorIdUseCase = mockk<BuscarVeiculoPorIdUseCase>()
    private val buscarVeiculoPorPlacaUseCase = mockk<BuscarVeiculoPorPlacaUseCase>()
    private val buscarVeiculosPorProprietarioUseCase = mockk<BuscarVeiculosPorProprietarioUseCase>()
    private val listarVeiculosUseCase = mockk<ListarVeiculosUseCase>()
    private val atualizarVeiculoUseCase = mockk<AtualizarVeiculoUseCase>()
    private val removerVeiculoUseCase = mockk<RemoverVeiculoUseCase>(relaxUnitFun = true)

    private val mockMvc =
        ApiTestSupport.mockMvc(
            VeiculoController(
                criarVeiculoUseCase = criarVeiculoUseCase,
                buscarVeiculoPorIdUseCase = buscarVeiculoPorIdUseCase,
                buscarVeiculoPorPlacaUseCase = buscarVeiculoPorPlacaUseCase,
                buscarVeiculosPorProprietarioUseCase = buscarVeiculosPorProprietarioUseCase,
                listarVeiculosUseCase = listarVeiculosUseCase,
                atualizarVeiculoUseCase = atualizarVeiculoUseCase,
                removerVeiculoUseCase = removerVeiculoUseCase,
            ),
        )

    @Test
    @DisplayName("Dado um veiculo valido, quando criar, entao deve retornar 201 com o corpo criado")
    fun givenValidVeiculo_whenCriar_thenReturn201WithBody() {
        val capturado = slot<VeiculoRequest>()
        every { criarVeiculoUseCase.executar(capture(capturado)) } returns ApiFixtures.veiculoResponse()

        mockMvc
            .perform(
                post("/veiculos")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(ApiTestSupport.json(ApiFixtures.veiculoRequest())),
            ).andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(ApiFixtures.VEICULO_ID))
            .andExpect(jsonPath("$.placa").value(ApiFixtures.PLACA))
            .andExpect(jsonPath("$.proprietario.id").value(ApiFixtures.PROPRIETARIO_ID))

        assertEquals(ApiFixtures.PROPRIETARIO_ID, capturado.captured.proprietarioId)
    }

    @Test
    @DisplayName("Dado uma placa em branco, quando criar, entao deve retornar 400 detalhando o campo")
    fun givenBlankPlaca_whenCriar_thenReturn400WithFieldDetails() {
        mockMvc
            .perform(
                post("/veiculos")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(ApiTestSupport.json(ApiFixtures.veiculoRequest(placa = ""))),
            ).andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.error").value("Dados inválidos"))
            .andExpect(jsonPath("$.fields.placa").exists())

        verify(exactly = 0) { criarVeiculoUseCase.executar(any()) }
    }

    @Test
    @DisplayName("Dado um id existente, quando buscar veiculo por id, entao deve retornar 200")
    fun givenExistingId_whenBuscarVeiculoPorId_thenReturn200() {
        every { buscarVeiculoPorIdUseCase.executar(ApiFixtures.VEICULO_ID) } returns ApiFixtures.veiculoResponse()

        mockMvc
            .perform(get("/veiculos/{id}", ApiFixtures.VEICULO_ID))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(ApiFixtures.VEICULO_ID))

        verify(exactly = 1) { buscarVeiculoPorIdUseCase.executar(ApiFixtures.VEICULO_ID) }
    }

    @Test
    @DisplayName("Dado um id inexistente, quando buscar veiculo por id, entao deve retornar 404")
    fun givenUnknownId_whenBuscarVeiculoPorId_thenReturn404() {
        every { buscarVeiculoPorIdUseCase.executar(any()) } throws EntityNotFoundException("Veículo não encontrado")

        mockMvc
            .perform(get("/veiculos/{id}", "inexistente"))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").value("Veículo não encontrado"))
    }

    @Test
    @DisplayName("Dado uma placa existente, quando buscar veiculo por placa, entao deve retornar 200")
    fun givenExistingPlaca_whenBuscarVeiculoPorPlaca_thenReturn200() {
        every { buscarVeiculoPorPlacaUseCase.executar(ApiFixtures.PLACA) } returns ApiFixtures.veiculoResponse()

        mockMvc
            .perform(get("/veiculos/placa/{placa}", ApiFixtures.PLACA))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.placa").value(ApiFixtures.PLACA))

        verify(exactly = 1) { buscarVeiculoPorPlacaUseCase.executar(ApiFixtures.PLACA) }
    }

    @Test
    @DisplayName("Dado um proprietario com veiculos, quando buscar por motorista, entao deve retornar 200")
    fun givenProprietarioWithVeiculos_whenBuscarPorMotorista_thenReturn200() {
        every { buscarVeiculosPorProprietarioUseCase.executar(ApiFixtures.PROPRIETARIO_ID) } returns
            listOf(ApiFixtures.veiculoResponse(), ApiFixtures.veiculoResponse(id = "outro-id", placa = "XYZ9876"))

        mockMvc
            .perform(get("/veiculos/motorista/{motoristaId}", ApiFixtures.PROPRIETARIO_ID))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[1].placa").value("XYZ9876"))

        verify(exactly = 1) { buscarVeiculosPorProprietarioUseCase.executar(ApiFixtures.PROPRIETARIO_ID) }
    }

    @Test
    @DisplayName("Dado veiculos cadastrados, quando listar todos, entao deve retornar 200 com a colecao")
    fun givenRegisteredVeiculos_whenListarTodos_thenReturn200WithCollection() {
        every { listarVeiculosUseCase.executar() } returns listOf(ApiFixtures.veiculoResponse())

        mockMvc
            .perform(get("/veiculos"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(1))

        verify(exactly = 1) { listarVeiculosUseCase.executar() }
    }

    @Test
    @DisplayName("Dado dados validos, quando atualizar, entao deve retornar 200 e delegar id e corpo")
    fun givenValidData_whenAtualizar_thenReturn200AndDelegateIdAndBody() {
        val id = slot<String>()
        val request = slot<VeiculoRequest>()
        every { atualizarVeiculoUseCase.executar(capture(id), capture(request)) } returns
            ApiFixtures.veiculoResponse(placa = "XYZ9876")

        mockMvc
            .perform(
                put("/veiculos/{id}", ApiFixtures.VEICULO_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(ApiTestSupport.json(ApiFixtures.veiculoRequest(placa = "XYZ9876"))),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.placa").value("XYZ9876"))

        assertEquals(ApiFixtures.VEICULO_ID, id.captured)
        assertEquals("XYZ9876", request.captured.placa)
    }

    @Test
    @DisplayName("Dado um id existente, quando remover, entao deve retornar 200 e delegar ao use case")
    fun givenExistingId_whenRemover_thenReturn200AndDelegate() {
        mockMvc
            .perform(delete("/veiculos/{id}", ApiFixtures.VEICULO_ID))
            .andExpect(status().isOk)

        verify(exactly = 1) { removerVeiculoUseCase.executar(ApiFixtures.VEICULO_ID) }
    }
}
