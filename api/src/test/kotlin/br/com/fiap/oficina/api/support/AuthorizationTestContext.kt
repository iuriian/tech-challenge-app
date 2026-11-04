package br.com.fiap.oficina.api.support

import br.com.fiap.oficina.api.config.SecurityConfig
import br.com.fiap.oficina.api.controller.ClienteController
import br.com.fiap.oficina.api.controller.FuncionarioController
import br.com.fiap.oficina.api.controller.OrdemServicoController
import br.com.fiap.oficina.api.controller.PecaController
import br.com.fiap.oficina.api.controller.ServicoController
import br.com.fiap.oficina.api.controller.VeiculoController
import br.com.fiap.oficina.api.exception.GlobalExceptionHandler
import br.com.fiap.oficina.api.observability.OrdemServicoTelemetry
import br.com.fiap.oficina.cliente.application.usecase.AtualizarClienteUseCase
import br.com.fiap.oficina.cliente.application.usecase.BuscarClientePorDocumentoUseCase
import br.com.fiap.oficina.cliente.application.usecase.BuscarClientePorIdUseCase
import br.com.fiap.oficina.cliente.application.usecase.BuscarClientePorNomeUseCase
import br.com.fiap.oficina.cliente.application.usecase.CriarClienteUseCase
import br.com.fiap.oficina.cliente.application.usecase.ListarClientesUseCase
import br.com.fiap.oficina.cliente.application.usecase.RemoverClienteUseCase
import br.com.fiap.oficina.funcionario.application.usecase.AtualizarFuncionarioUseCase
import br.com.fiap.oficina.funcionario.application.usecase.BuscarFuncionarioPorIdUseCase
import br.com.fiap.oficina.funcionario.application.usecase.BuscarFuncionarioPorNomeUseCase
import br.com.fiap.oficina.funcionario.application.usecase.CriarFuncionarioUseCase
import br.com.fiap.oficina.funcionario.application.usecase.ListarFuncionariosUseCase
import br.com.fiap.oficina.funcionario.application.usecase.RemoverFuncionarioUseCase
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
import br.com.fiap.oficina.peca.application.usecase.AtualizarPecaUseCase
import br.com.fiap.oficina.peca.application.usecase.AtualizarStatusPecaUseCase
import br.com.fiap.oficina.peca.application.usecase.BuscarPecaPorCodigoUseCase
import br.com.fiap.oficina.peca.application.usecase.CadastrarPecaUseCase
import br.com.fiap.oficina.peca.application.usecase.EstoquePecasUseCase
import br.com.fiap.oficina.peca.application.usecase.ListarPecasUseCase
import br.com.fiap.oficina.servico.application.usecase.AtualizarServicoUseCase
import br.com.fiap.oficina.servico.application.usecase.BuscarServicoUseCase
import br.com.fiap.oficina.servico.application.usecase.CriarServicoUseCase
import br.com.fiap.oficina.servico.application.usecase.DesativarServicoUseCase
import br.com.fiap.oficina.servico.application.usecase.ListarServicosAtivosUseCase
import br.com.fiap.oficina.servico.application.usecase.ListarTodosServicosUseCase
import br.com.fiap.oficina.servico.application.usecase.ReativarServicoUseCase
import br.com.fiap.oficina.veiculo.application.usecase.AtualizarVeiculoUseCase
import br.com.fiap.oficina.veiculo.application.usecase.BuscarVeiculoPorIdUseCase
import br.com.fiap.oficina.veiculo.application.usecase.BuscarVeiculoPorPlacaUseCase
import br.com.fiap.oficina.veiculo.application.usecase.BuscarVeiculosPorProprietarioUseCase
import br.com.fiap.oficina.veiculo.application.usecase.CriarVeiculoUseCase
import br.com.fiap.oficina.veiculo.application.usecase.ListarVeiculosUseCase
import br.com.fiap.oficina.veiculo.application.usecase.RemoverVeiculoUseCase
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.mockk.mockk
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
@EnableWebMvc
@Import(SecurityConfig::class)
internal open class AuthorizationTestContext : WebMvcConfigurer {
    override fun configureMessageConverters(converters: MutableList<HttpMessageConverter<*>>) {
        converters.add(MappingJackson2HttpMessageConverter(objectMapper()))
    }

    private fun objectMapper(): ObjectMapper = ObjectMapper()
        .registerKotlinModule()
        .registerModule(JavaTimeModule())

    @Bean
    open fun jwtDecoder(): JwtDecoder = mockk(relaxed = true)

    @Bean
    open fun globalExceptionHandler(): GlobalExceptionHandler = GlobalExceptionHandler()

    // ------------------------------------------------------------------ cliente
    @Bean open fun criarClienteUseCase(): CriarClienteUseCase = mockk(relaxed = true)

    @Bean open fun listarClientesUseCase(): ListarClientesUseCase = mockk(relaxed = true)

    @Bean open fun buscarClientePorIdUseCase(): BuscarClientePorIdUseCase = mockk(relaxed = true)

    @Bean open fun buscarClientePorNomeUseCase(): BuscarClientePorNomeUseCase = mockk(relaxed = true)

    @Bean open fun buscarClientePorDocumentoUseCase(): BuscarClientePorDocumentoUseCase = mockk(relaxed = true)

    @Bean open fun atualizarClienteUseCase(): AtualizarClienteUseCase = mockk(relaxed = true)

    @Bean open fun removerClienteUseCase(): RemoverClienteUseCase = mockk(relaxed = true)

    // ------------------------------------------------------------- funcionario
    @Bean open fun criarFuncionarioUseCase(): CriarFuncionarioUseCase = mockk(relaxed = true)

    @Bean open fun listarFuncionariosUseCase(): ListarFuncionariosUseCase = mockk(relaxed = true)

    @Bean open fun buscarFuncionarioPorIdUseCase(): BuscarFuncionarioPorIdUseCase = mockk(relaxed = true)

    @Bean open fun buscarFuncionarioPorNomeUseCase(): BuscarFuncionarioPorNomeUseCase = mockk(relaxed = true)

    @Bean open fun atualizarFuncionarioUseCase(): AtualizarFuncionarioUseCase = mockk(relaxed = true)

    @Bean open fun removerFuncionarioUseCase(): RemoverFuncionarioUseCase = mockk(relaxed = true)

    // -------------------------------------------------------------------- peca
    @Bean open fun cadastrarPecaUseCase(): CadastrarPecaUseCase = mockk(relaxed = true)

    @Bean open fun atualizarPecaUseCase(): AtualizarPecaUseCase = mockk(relaxed = true)

    @Bean open fun listarPecasUseCase(): ListarPecasUseCase = mockk(relaxed = true)

    @Bean open fun buscarPecaPorCodigoUseCase(): BuscarPecaPorCodigoUseCase = mockk(relaxed = true)

    @Bean open fun atualizarStatusPecaUseCase(): AtualizarStatusPecaUseCase = mockk(relaxed = true)

    @Bean open fun estoquePecasUseCase(): EstoquePecasUseCase = mockk(relaxed = true)

    // ----------------------------------------------------------------- veiculo
    @Bean open fun criarVeiculoUseCase(): CriarVeiculoUseCase = mockk(relaxed = true)

    @Bean open fun buscarVeiculoPorIdUseCase(): BuscarVeiculoPorIdUseCase = mockk(relaxed = true)

    @Bean open fun buscarVeiculoPorPlacaUseCase(): BuscarVeiculoPorPlacaUseCase = mockk(relaxed = true)

    @Bean
    open fun buscarVeiculosPorProprietarioUseCase(): BuscarVeiculosPorProprietarioUseCase = mockk(relaxed = true)

    @Bean open fun listarVeiculosUseCase(): ListarVeiculosUseCase = mockk(relaxed = true)

    @Bean open fun atualizarVeiculoUseCase(): AtualizarVeiculoUseCase = mockk(relaxed = true)

    @Bean open fun removerVeiculoUseCase(): RemoverVeiculoUseCase = mockk(relaxed = true)

    // ----------------------------------------------------------------- servico
    @Bean open fun criarServicoUseCase(): CriarServicoUseCase = mockk(relaxed = true)

    @Bean open fun listarServicosAtivosUseCase(): ListarServicosAtivosUseCase = mockk(relaxed = true)

    @Bean open fun listarTodosServicosUseCase(): ListarTodosServicosUseCase = mockk(relaxed = true)

    @Bean open fun buscarServicoUseCase(): BuscarServicoUseCase = mockk(relaxed = true)

    @Bean open fun atualizarServicoUseCase(): AtualizarServicoUseCase = mockk(relaxed = true)

    @Bean open fun desativarServicoUseCase(): DesativarServicoUseCase = mockk(relaxed = true)

    @Bean open fun reativarServicoUseCase(): ReativarServicoUseCase = mockk(relaxed = true)

    // ----------------------------------------------------------- ordem servico
    @Bean open fun criarOrdemServicoUseCase(): CriarOrdemServicoUseCase = mockk(relaxed = true)

    @Bean open fun listarOrdemServicoUseCase(): ListarOrdemServicoUseCase = mockk(relaxed = true)

    @Bean open fun buscarOrdemServicoPorIdUseCase(): BuscarOrdemServicoPorIdUseCase = mockk(relaxed = true)

    @Bean
    open fun buscarOrdemServicoPorClienteUseCase(): BuscarOrdemServicoPorClienteUseCase = mockk(relaxed = true)

    @Bean open fun obterOrcamentoUseCase(): ObterOrcamentoUseCase = mockk(relaxed = true)

    @Bean
    open fun calcularTempoMedioExecucaoUseCase(): CalcularTempoMedioExecucaoUseCase = mockk(relaxed = true)

    @Bean open fun atualizarOrdemServicoUseCase(): AtualizarOrdemServicoUseCase = mockk(relaxed = true)

    @Bean
    open fun avancarStatusOrdemServicoUseCase(): AvancarStatusOrdemServicoUseCase = mockk(relaxed = true)

    @Bean
    open fun alterarStatusOrdemServicoUseCase(): AlterarStatusOrdemServicoUseCase = mockk(relaxed = true)

    @Bean open fun deletarOrdemServicoUseCase(): DeletarOrdemServicoUseCase = mockk(relaxed = true)

    @Bean open fun ordemServicoTelemetry(): OrdemServicoTelemetry = mockk(relaxed = true)

    // ------------------------------------------------------------- controllers
    @Bean
    open fun clienteController(
        criar: CriarClienteUseCase,
        listar: ListarClientesUseCase,
        porId: BuscarClientePorIdUseCase,
        porNome: BuscarClientePorNomeUseCase,
        porDocumento: BuscarClientePorDocumentoUseCase,
        atualizar: AtualizarClienteUseCase,
        remover: RemoverClienteUseCase,
    ) = ClienteController(criar, listar, porId, porNome, porDocumento, atualizar, remover)

    @Bean
    open fun funcionarioController(
        criar: CriarFuncionarioUseCase,
        listar: ListarFuncionariosUseCase,
        porId: BuscarFuncionarioPorIdUseCase,
        porNome: BuscarFuncionarioPorNomeUseCase,
        atualizar: AtualizarFuncionarioUseCase,
        remover: RemoverFuncionarioUseCase,
    ) = FuncionarioController(criar, listar, porId, porNome, atualizar, remover)

    @Bean
    open fun pecaController(
        cadastrar: CadastrarPecaUseCase,
        atualizar: AtualizarPecaUseCase,
        listar: ListarPecasUseCase,
        porCodigo: BuscarPecaPorCodigoUseCase,
        status: AtualizarStatusPecaUseCase,
        estoque: EstoquePecasUseCase,
    ) = PecaController(cadastrar, atualizar, listar, porCodigo, status, estoque)

    @Bean
    open fun veiculoController(
        criar: CriarVeiculoUseCase,
        porId: BuscarVeiculoPorIdUseCase,
        porPlaca: BuscarVeiculoPorPlacaUseCase,
        porProprietario: BuscarVeiculosPorProprietarioUseCase,
        listar: ListarVeiculosUseCase,
        atualizar: AtualizarVeiculoUseCase,
        remover: RemoverVeiculoUseCase,
    ) = VeiculoController(criar, porId, porPlaca, porProprietario, listar, atualizar, remover)

    @Bean
    open fun servicoController(
        criar: CriarServicoUseCase,
        ativos: ListarServicosAtivosUseCase,
        todos: ListarTodosServicosUseCase,
        buscar: BuscarServicoUseCase,
        atualizar: AtualizarServicoUseCase,
        desativar: DesativarServicoUseCase,
        reativar: ReativarServicoUseCase,
    ) = ServicoController(criar, ativos, todos, buscar, atualizar, desativar, reativar)

    @Bean
    @Suppress("LongParameterList")
    open fun ordemServicoController(
        criar: CriarOrdemServicoUseCase,
        listar: ListarOrdemServicoUseCase,
        porId: BuscarOrdemServicoPorIdUseCase,
        porCliente: BuscarOrdemServicoPorClienteUseCase,
        orcamento: ObterOrcamentoUseCase,
        tempoMedio: CalcularTempoMedioExecucaoUseCase,
        atualizar: AtualizarOrdemServicoUseCase,
        avancar: AvancarStatusOrdemServicoUseCase,
        alterar: AlterarStatusOrdemServicoUseCase,
        deletar: DeletarOrdemServicoUseCase,
        telemetry: OrdemServicoTelemetry,
    ) = OrdemServicoController(
        criar,
        listar,
        porId,
        porCliente,
        orcamento,
        tempoMedio,
        atualizar,
        avancar,
        alterar,
        deletar,
        telemetry,
    )
}
