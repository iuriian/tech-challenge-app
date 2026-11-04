package br.com.fiap.oficina.api.security

import br.com.fiap.oficina.api.support.ApiFixtures
import br.com.fiap.oficina.api.support.ApiTestSupport
import br.com.fiap.oficina.api.support.AuthorizationTestContext
import br.com.fiap.oficina.cliente.application.usecase.AtualizarClienteUseCase
import br.com.fiap.oficina.cliente.application.usecase.BuscarClientePorDocumentoUseCase
import br.com.fiap.oficina.cliente.application.usecase.BuscarClientePorIdUseCase
import br.com.fiap.oficina.cliente.application.usecase.BuscarClientePorNomeUseCase
import br.com.fiap.oficina.cliente.application.usecase.CriarClienteUseCase
import br.com.fiap.oficina.cliente.application.usecase.ListarClientesUseCase
import br.com.fiap.oficina.funcionario.application.usecase.AtualizarFuncionarioUseCase
import br.com.fiap.oficina.funcionario.application.usecase.BuscarFuncionarioPorIdUseCase
import br.com.fiap.oficina.funcionario.application.usecase.BuscarFuncionarioPorNomeUseCase
import br.com.fiap.oficina.funcionario.application.usecase.CriarFuncionarioUseCase
import br.com.fiap.oficina.funcionario.application.usecase.ListarFuncionariosUseCase
import br.com.fiap.oficina.ordemservico.application.usecase.AlterarStatusOrdemServicoUseCase
import br.com.fiap.oficina.ordemservico.application.usecase.AtualizarOrdemServicoUseCase
import br.com.fiap.oficina.ordemservico.application.usecase.AvancarStatusOrdemServicoUseCase
import br.com.fiap.oficina.ordemservico.application.usecase.BuscarOrdemServicoPorClienteUseCase
import br.com.fiap.oficina.ordemservico.application.usecase.BuscarOrdemServicoPorIdUseCase
import br.com.fiap.oficina.ordemservico.application.usecase.CalcularTempoMedioExecucaoUseCase
import br.com.fiap.oficina.ordemservico.application.usecase.CriarOrdemServicoUseCase
import br.com.fiap.oficina.ordemservico.application.usecase.ListarOrdemServicoUseCase
import br.com.fiap.oficina.ordemservico.application.usecase.ObterOrcamentoUseCase
import br.com.fiap.oficina.peca.application.usecase.AtualizarPecaUseCase
import br.com.fiap.oficina.peca.application.usecase.AtualizarStatusPecaUseCase
import br.com.fiap.oficina.peca.application.usecase.BuscarPecaPorCodigoUseCase
import br.com.fiap.oficina.peca.application.usecase.CadastrarPecaUseCase
import br.com.fiap.oficina.peca.application.usecase.EstoquePecasUseCase
import br.com.fiap.oficina.peca.application.usecase.ListarPecasUseCase
import br.com.fiap.oficina.servico.application.usecase.AtualizarServicoUseCase
import br.com.fiap.oficina.servico.application.usecase.BuscarServicoUseCase
import br.com.fiap.oficina.servico.application.usecase.CriarServicoUseCase
import br.com.fiap.oficina.servico.application.usecase.ListarServicosAtivosUseCase
import br.com.fiap.oficina.servico.application.usecase.ListarTodosServicosUseCase
import br.com.fiap.oficina.servico.application.usecase.ReativarServicoUseCase
import br.com.fiap.oficina.veiculo.application.usecase.AtualizarVeiculoUseCase
import br.com.fiap.oficina.veiculo.application.usecase.BuscarVeiculoPorIdUseCase
import br.com.fiap.oficina.veiculo.application.usecase.BuscarVeiculoPorPlacaUseCase
import br.com.fiap.oficina.veiculo.application.usecase.BuscarVeiculosPorProprietarioUseCase
import br.com.fiap.oficina.veiculo.application.usecase.CriarVeiculoUseCase
import br.com.fiap.oficina.veiculo.application.usecase.ListarVeiculosUseCase
import io.mockk.every
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.DynamicNode
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

/**
 * Matriz de autorização: para cada operação de cada contrato, todo perfil conhecido é
 * exercitado e deve receber 2xx quando o `@RolesAllowed` o lista, 403 quando não.
 *
 * O teste roda sobre o [AuthorizationTestContext], que importa o `SecurityConfig` real
 * — cadeia de filtros e method security de verdade, diferente dos testes de controller,
 * que usam MockMvc standalone e não enxergam autorização nenhuma.
 */
@ExtendWith(SpringExtension::class)
@WebAppConfiguration
@ContextConfiguration(classes = [AuthorizationTestContext::class])
@DisplayName("Autorizacao - matriz de perfis por endpoint")
class AuthorizationRulesTest {
    @Autowired private lateinit var context: WebApplicationContext

    @Autowired private lateinit var criarCliente: CriarClienteUseCase

    @Autowired private lateinit var listarClientes: ListarClientesUseCase

    @Autowired private lateinit var clientePorId: BuscarClientePorIdUseCase

    @Autowired private lateinit var clientePorNome: BuscarClientePorNomeUseCase

    @Autowired private lateinit var clientePorDocumento: BuscarClientePorDocumentoUseCase

    @Autowired private lateinit var atualizarCliente: AtualizarClienteUseCase

    @Autowired private lateinit var criarFuncionario: CriarFuncionarioUseCase

    @Autowired private lateinit var listarFuncionarios: ListarFuncionariosUseCase

    @Autowired private lateinit var funcionarioPorId: BuscarFuncionarioPorIdUseCase

    @Autowired private lateinit var funcionarioPorNome: BuscarFuncionarioPorNomeUseCase

    @Autowired private lateinit var atualizarFuncionario: AtualizarFuncionarioUseCase

    @Autowired private lateinit var cadastrarPeca: CadastrarPecaUseCase

    @Autowired private lateinit var atualizarPeca: AtualizarPecaUseCase

    @Autowired private lateinit var listarPecas: ListarPecasUseCase

    @Autowired private lateinit var pecaPorCodigo: BuscarPecaPorCodigoUseCase

    @Autowired private lateinit var statusPeca: AtualizarStatusPecaUseCase

    @Autowired private lateinit var estoquePeca: EstoquePecasUseCase

    @Autowired private lateinit var criarVeiculo: CriarVeiculoUseCase

    @Autowired private lateinit var veiculoPorId: BuscarVeiculoPorIdUseCase

    @Autowired private lateinit var veiculoPorPlaca: BuscarVeiculoPorPlacaUseCase

    @Autowired private lateinit var veiculosPorProprietario: BuscarVeiculosPorProprietarioUseCase

    @Autowired private lateinit var listarVeiculos: ListarVeiculosUseCase

    @Autowired private lateinit var atualizarVeiculo: AtualizarVeiculoUseCase

    @Autowired private lateinit var criarServico: CriarServicoUseCase

    @Autowired private lateinit var servicosAtivos: ListarServicosAtivosUseCase

    @Autowired private lateinit var todosServicos: ListarTodosServicosUseCase

    @Autowired private lateinit var buscarServico: BuscarServicoUseCase

    @Autowired private lateinit var atualizarServico: AtualizarServicoUseCase

    @Autowired private lateinit var reativarServico: ReativarServicoUseCase

    @Autowired private lateinit var criarOrdem: CriarOrdemServicoUseCase

    @Autowired private lateinit var listarOrdens: ListarOrdemServicoUseCase

    @Autowired private lateinit var ordemPorId: BuscarOrdemServicoPorIdUseCase

    @Autowired private lateinit var ordensPorCliente: BuscarOrdemServicoPorClienteUseCase

    @Autowired private lateinit var orcamento: ObterOrcamentoUseCase

    @Autowired private lateinit var tempoMedio: CalcularTempoMedioExecucaoUseCase

    @Autowired private lateinit var atualizarOrdem: AtualizarOrdemServicoUseCase

    @Autowired private lateinit var avancarStatus: AvancarStatusOrdemServicoUseCase

    @Autowired private lateinit var alterarStatus: AlterarStatusOrdemServicoUseCase

    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply<DefaultMockMvcBuilder>(springSecurity())
            .build()

        // Os use cases devolvem massa real para que o caminho permitido termine em 2xx:
        // sem isso um erro de serializacao viraria 500 e passaria por "nao e 403".
        every { criarCliente.executar(any()) } returns ApiFixtures.clienteResponse()
        every { listarClientes.executar() } returns listOf(ApiFixtures.clienteResponse())
        every { clientePorId.executar(any()) } returns ApiFixtures.clienteResponse()
        every { clientePorNome.executar(any()) } returns ApiFixtures.clienteResponse()
        every { clientePorDocumento.executar(any()) } returns ApiFixtures.clienteResponse()
        every { atualizarCliente.executar(any(), any()) } returns ApiFixtures.clienteResponse()

        every { criarFuncionario.executar(any()) } returns ApiFixtures.funcionarioResponse()
        every { listarFuncionarios.executar() } returns listOf(ApiFixtures.funcionarioResponse())
        every { funcionarioPorId.executar(any()) } returns ApiFixtures.funcionarioResponse()
        every { funcionarioPorNome.executar(any()) } returns ApiFixtures.funcionarioResponse()
        every { atualizarFuncionario.executar(any(), any()) } returns ApiFixtures.funcionarioResponse()

        every { cadastrarPeca.executar(any()) } returns ApiFixtures.pecaResponse()
        every { atualizarPeca.executar(any(), any()) } returns ApiFixtures.pecaResponse()
        every { listarPecas.executar() } returns listOf(ApiFixtures.pecaResponse())
        every { pecaPorCodigo.executar(any()) } returns ApiFixtures.pecaResponse()
        every { statusPeca.executar(any(), any()) } returns ApiFixtures.pecaResponse()
        every { estoquePeca.executar(any(), any()) } returns ApiFixtures.pecaResponse()

        every { criarVeiculo.executar(any()) } returns ApiFixtures.veiculoResponse()
        every { veiculoPorId.executar(any()) } returns ApiFixtures.veiculoResponse()
        every { veiculoPorPlaca.executar(any()) } returns ApiFixtures.veiculoResponse()
        every { veiculosPorProprietario.executar(any()) } returns listOf(ApiFixtures.veiculoResponse())
        every { listarVeiculos.executar() } returns listOf(ApiFixtures.veiculoResponse())
        every { atualizarVeiculo.executar(any(), any()) } returns ApiFixtures.veiculoResponse()

        every { criarServico.executar(any()) } returns ApiFixtures.servicoResponse()
        every { servicosAtivos.executar() } returns listOf(ApiFixtures.servicoResponse())
        every { todosServicos.executar() } returns listOf(ApiFixtures.servicoResponse())
        every { buscarServico.executar(any()) } returns ApiFixtures.servicoResponse()
        every { atualizarServico.executar(any(), any()) } returns ApiFixtures.servicoResponse()
        every { reativarServico.executar(any()) } returns ApiFixtures.servicoResponse()

        every { criarOrdem.executar(any()) } returns ApiFixtures.ordemServicoResponse()
        every { listarOrdens.executar() } returns listOf(ApiFixtures.ordemServicoResponse())
        every { ordemPorId.executar(any()) } returns ApiFixtures.ordemServicoResponse()
        every { ordensPorCliente.executar(any()) } returns listOf(ApiFixtures.ordemServicoResponse())
        every { orcamento.executar(any()) } returns ApiFixtures.orcamentoResponse()
        every { tempoMedio.executar() } returns ApiFixtures.tempoMedioExecucaoResponse()
        every { atualizarOrdem.executar(any(), any()) } returns ApiFixtures.ordemServicoResponse()
        every { avancarStatus.executar(any()) } returns ApiFixtures.ordemServicoResponse()
        every { alterarStatus.executar(any(), any()) } returns ApiFixtures.ordemServicoResponse()
    }

    @TestFactory
    @DisplayName("Clientes")
    fun clientes(): List<DynamicNode> = matriz(
        caso("POST", "/api/v2/clientes", ATENDENTE_ADMIN) { post(it).corpo(ApiFixtures.clienteRequest()) },
        caso("GET", "/api/v2/clientes", ATENDENTE_ADMIN) { get(it) },
        caso("GET", "/api/v2/clientes/{id}", ATENDENTE_ADMIN) { get(it, ApiFixtures.CLIENTE_ID) },
        caso("GET", "/api/v2/clientes/nome/{nome}", ATENDENTE_ADMIN) { get(it, ApiFixtures.NOME_CLIENTE) },
        caso("GET", "/api/v2/clientes/documento/{doc}", ATENDENTE_ADMIN) { get(it, ApiFixtures.CPF) },
        caso("PUT", "/api/v2/clientes/{id}", ATENDENTE_ADMIN) {
            put(it, ApiFixtures.CLIENTE_ID).corpo(ApiFixtures.clienteRequest())
        },
        caso("DELETE", "/api/v2/clientes/{id}", SO_ADMIN) { delete(it, ApiFixtures.CLIENTE_ID) },
    )

    @TestFactory
    @DisplayName("Funcionarios")
    fun funcionarios(): List<DynamicNode> = matriz(
        caso("POST", "/api/v2/funcionarios", ATENDENTE_ADMIN) { post(it).corpo(ApiFixtures.funcionarioRequest()) },
        caso("GET", "/api/v2/funcionarios", ATENDENTE_MECANICO_ADMIN) { get(it) },
        caso("GET", "/api/v2/funcionarios/id/{id}", ATENDENTE_MECANICO_ADMIN) {
            get(it, ApiFixtures.FUNCIONARIO_ID)
        },
        caso("GET", "/api/v2/funcionarios/nome/{nome}", ATENDENTE_MECANICO_ADMIN) { get(it, "Vini Jr.") },
        caso("PUT", "/api/v2/funcionarios/{id}", ATENDENTE_ADMIN) {
            put(it, ApiFixtures.FUNCIONARIO_ID).corpo(ApiFixtures.funcionarioRequest())
        },
        caso("DELETE", "/api/v2/funcionarios/{id}", ATENDENTE_ADMIN) { delete(it, ApiFixtures.FUNCIONARIO_ID) },
    )

    @TestFactory
    @DisplayName("Pecas")
    fun pecas(): List<DynamicNode> = matriz(
        caso("POST", "/api/v2/pecas", SO_ADMIN) { post(it).corpo(ApiFixtures.pecaRequest()) },
        caso("PUT", "/api/v2/pecas/{codigo}", SO_ADMIN) {
            put(it, ApiFixtures.CODIGO_PECA).corpo(ApiFixtures.pecaRequest())
        },
        caso("DELETE", "/api/v2/pecas/{codigo}", SO_ADMIN) { delete(it, ApiFixtures.CODIGO_PECA) },
        caso("GET", "/api/v2/pecas", ATENDENTE_MECANICO_ADMIN) { get(it) },
        caso("GET", "/api/v2/pecas/codigo/{codigo}", ATENDENTE_MECANICO_ADMIN) { get(it, ApiFixtures.CODIGO_PECA) },
        caso("PATCH", "/api/v2/pecas/{codigo}/estoque/retirar", ATENDENTE_MECANICO_ADMIN) {
            patch(it, ApiFixtures.CODIGO_PECA).param("qtd", "5")
        },
        caso("PATCH", "/api/v2/pecas/{codigo}/estoque/repor", ATENDENTE_MECANICO_ADMIN) {
            patch(it, ApiFixtures.CODIGO_PECA).param("qtd", "10")
        },
        caso("PATCH", "/api/v2/pecas/{codigo}/reativar", SO_ADMIN) { patch(it, ApiFixtures.CODIGO_PECA) },
    )

    @TestFactory
    @DisplayName("Veiculos")
    fun veiculos(): List<DynamicNode> = matriz(
        caso("POST", "/api/v2/veiculos", ATENDENTE_ADMIN) { post(it).corpo(ApiFixtures.veiculoRequest()) },
        caso("GET", "/api/v2/veiculos", ATENDENTE_ADMIN) { get(it) },
        caso("GET", "/api/v2/veiculos/{id}", ATENDENTE_ADMIN) { get(it, ApiFixtures.VEICULO_ID) },
        caso("GET", "/api/v2/veiculos/placa/{placa}", ATENDENTE_ADMIN) { get(it, ApiFixtures.PLACA) },
        caso("GET", "/api/v2/veiculos/motorista/{motoristaId}", ATENDENTE_ADMIN) {
            get(it, ApiFixtures.PROPRIETARIO_ID)
        },
        caso("PUT", "/api/v2/veiculos/{id}", ATENDENTE_ADMIN) {
            put(it, ApiFixtures.VEICULO_ID).corpo(ApiFixtures.veiculoRequest())
        },
        caso("DELETE", "/api/v2/veiculos/{id}", SO_ADMIN) { delete(it, ApiFixtures.VEICULO_ID) },
    )

    @TestFactory
    @DisplayName("Catalogo de servicos")
    fun catalogoDeServicos(): List<DynamicNode> = matriz(
        caso("POST", "/api/v2/catalogo/servicos", SO_ADMIN) { post(it).corpo(ApiFixtures.servicoRequest()) },
        caso("GET", "/api/v2/catalogo/servicos", ATENDENTE_MECANICO_ADMIN) { get(it) },
        caso("GET", "/api/v2/catalogo/servicos/todos", SO_ADMIN) { get(it) },
        caso("GET", "/api/v2/catalogo/servicos/{id}", ATENDENTE_MECANICO_ADMIN) { get(it, ApiFixtures.SERVICO_ID) },
        caso("PUT", "/api/v2/catalogo/servicos/{id}", SO_ADMIN) {
            put(it, ApiFixtures.SERVICO_ID).corpo(ApiFixtures.servicoRequest())
        },
        caso("DELETE", "/api/v2/catalogo/servicos/{id}", SO_ADMIN) { delete(it, ApiFixtures.SERVICO_ID) },
        caso("PATCH", "/api/v2/catalogo/servicos/{id}/reativar", SO_ADMIN) { patch(it, ApiFixtures.SERVICO_ID) },
    )

    @TestFactory
    @DisplayName("Ordens de servico")
    fun ordensDeServico(): List<DynamicNode> = matriz(
        caso("POST", "/api/v2/servicos", ATENDENTE_ADMIN) { post(it).corpo(ApiFixtures.ordemServicoRequest()) },
        caso("GET", "/api/v2/servicos", ATENDENTE_ADMIN) { get(it) },
        caso("GET", "/api/v2/servicos/{id}", ATENDENTE_ADMIN_CLIENTE) { get(it, ApiFixtures.ORDEM_SERVICO_ID) },
        caso("GET", "/api/v2/servicos/cliente/{clienteId}", ATENDENTE_ADMIN_CLIENTE) {
            get(it, ApiFixtures.CLIENTE_ID)
        },
        caso("GET", "/api/v2/servicos/{id}/orcamento", ATENDENTE_ADMIN_CLIENTE) {
            get(it, ApiFixtures.ORDEM_SERVICO_ID)
        },
        caso("GET", "/api/v2/servicos/metricas/tempo-medio", ATENDENTE_ADMIN) { get(it) },
        caso("PUT", "/api/v2/servicos/{id}", ATENDENTE_ADMIN) {
            put(it, ApiFixtures.ORDEM_SERVICO_ID).corpo(ApiFixtures.ordemServicoRequest())
        },
        caso("PATCH", "/api/v2/servicos/{id}/avancar", ATENDENTE_MECANICO_ADMIN) {
            patch(it, ApiFixtures.ORDEM_SERVICO_ID)
        },
        caso("PATCH", "/api/v2/servicos/{id}/status", ATENDENTE_ADMIN_CLIENTE) {
            patch(it, ApiFixtures.ORDEM_SERVICO_ID).corpo(ApiFixtures.alterarStatusRequest())
        },
        caso("DELETE", "/api/v2/servicos/{id}", ATENDENTE_ADMIN) { delete(it, ApiFixtures.ORDEM_SERVICO_ID) },
    )

    @Test
    @DisplayName("Dado nenhum token, quando chamar endpoint protegido, entao deve retornar 401")
    fun givenNoToken_whenCallingProtectedEndpoint_thenReturn401() {
        mockMvc.perform(get("/api/v2/clientes")).andExpect(status().isUnauthorized)
        mockMvc.perform(get("/api/v2/veiculos")).andExpect(status().isUnauthorized)
        mockMvc.perform(get("/api/v2/servicos")).andExpect(status().isUnauthorized)
        mockMvc.perform(get("/api/v2/catalogo/servicos")).andExpect(status().isUnauthorized)
    }

    @Test
    @DisplayName("Dado um perfil desconhecido, quando chamar endpoint protegido, entao deve retornar 403")
    fun givenUnknownRole_whenCallingProtectedEndpoint_thenReturn403() {
        mockMvc
            .perform(get("/api/v2/clientes").with(user("probe").roles("VISITANTE")))
            .andExpect(status().isForbidden)
    }

    @Test
    @DisplayName("Dado um perfil sem permissao, quando negado, entao o corpo deve seguir o contrato de erro")
    fun givenRoleWithoutPermission_whenDenied_thenBodyFollowsErrorContract() {
        mockMvc
            .perform(delete("/api/v2/clientes/{id}", ApiFixtures.CLIENTE_ID).with(user("probe").roles("ATENDENTE")))
            .andExpect(status().isForbidden)
            .andExpect(jsonPath("$.status").value(403))
            .andExpect(jsonPath("$.error").value("Perfil sem permissão para a operação"))
            .andExpect(jsonPath("$.path").value("/api/v2/clientes/${ApiFixtures.CLIENTE_ID}"))
            .andExpect(jsonPath("$.timestamp").exists())
    }

    @Test
    @DisplayName("Dado os endpoints publicos, quando chamados sem token, entao nao devem retornar 401")
    fun givenPublicEndpoints_whenCalledWithoutToken_thenNoneReturn401() {
        listOf("/actuator/health", "/actuator/info", "/v3/api-docs", "/swagger-ui/index.html").forEach { caminho ->
            val resultado = mockMvc.perform(get(caminho)).andReturn()
            check(resultado.response.status != 401) { "$caminho deveria ser publico, veio 401" }
        }
    }

    // ------------------------------------------------------------------ helpers

    private data class Caso(
        val metodo: String,
        val padrao: String,
        val permitidos: Set<String>,
        val builder: (String) -> MockHttpServletRequestBuilder,
    )

    private fun caso(
        metodo: String,
        padrao: String,
        permitidos: Set<String>,
        builder: (String) -> MockHttpServletRequestBuilder,
    ) = Caso(metodo, padrao, permitidos, builder)

    private fun MockHttpServletRequestBuilder.corpo(valor: Any): MockHttpServletRequestBuilder =
        contentType(MediaType.APPLICATION_JSON).content(ApiTestSupport.json(valor))

    /** Expande cada caso em um teste por perfil conhecido. */
    private fun matriz(vararg casos: Caso): List<DynamicNode> = casos.flatMap { caso ->
        PERFIS.map { perfil ->
            val permitido = perfil in caso.permitidos
            val esperado = if (permitido) "2xx" else "403"

            dynamicTest("${caso.metodo} ${caso.padrao} como $perfil -> $esperado") {
                val resultado = mockMvc
                    .perform(caso.builder(caso.padrao).with(user("probe").roles(perfil)))
                    .andReturn()

                val status = resultado.response.status
                if (permitido) {
                    check(status in 200..299) {
                        "${caso.metodo} ${caso.padrao} como $perfil deveria passar, veio $status " +
                            "(${resultado.resolvedException})"
                    }
                } else {
                    check(status == 403) {
                        "${caso.metodo} ${caso.padrao} como $perfil deveria ser 403, veio $status " +
                            "(${resultado.resolvedException})"
                    }
                }
            }
        }
    }

    private companion object {
        val PERFIS = listOf("ADMIN", "ATENDENTE", "MECANICO", "CLIENTE")

        val SO_ADMIN = setOf("ADMIN")
        val ATENDENTE_ADMIN = setOf("ATENDENTE", "ADMIN")
        val ATENDENTE_MECANICO_ADMIN = setOf("ATENDENTE", "MECANICO", "ADMIN")
        val ATENDENTE_ADMIN_CLIENTE = setOf("ATENDENTE", "ADMIN", "CLIENTE")
    }
}
