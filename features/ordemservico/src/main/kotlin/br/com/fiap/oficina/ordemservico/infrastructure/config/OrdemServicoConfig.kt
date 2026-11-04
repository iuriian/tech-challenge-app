package br.com.fiap.oficina.ordemservico.infrastructure.config

import br.com.fiap.oficina.cliente.application.port.ClienteQueryPort
import br.com.fiap.oficina.funcionario.application.port.FuncionarioQueryPort
import br.com.fiap.oficina.ordemservico.domain.OrdemServicoRepository
import br.com.fiap.oficina.ordemservico.domain.SequenciaOrdemServico
import br.com.fiap.oficina.ordemservico.domain.port.cliente.ConsultarCliente
import br.com.fiap.oficina.ordemservico.domain.port.funcionario.ConsultarFuncionario
import br.com.fiap.oficina.ordemservico.domain.port.peca.ConsultarPeca
import br.com.fiap.oficina.ordemservico.domain.port.servico.ConsultarServico
import br.com.fiap.oficina.ordemservico.domain.port.veiculo.ConsultarVeiculo
import br.com.fiap.oficina.ordemservico.infrastructure.acl.cliente.ClienteMapper
import br.com.fiap.oficina.ordemservico.infrastructure.acl.cliente.ConsultarClienteAdapter
import br.com.fiap.oficina.ordemservico.infrastructure.acl.funcionario.ConsultarFuncionarioAdapter
import br.com.fiap.oficina.ordemservico.infrastructure.acl.funcionario.FuncionarioMapper
import br.com.fiap.oficina.ordemservico.infrastructure.acl.peca.ConsultarPecaAdapter
import br.com.fiap.oficina.ordemservico.infrastructure.acl.peca.PecaMapper
import br.com.fiap.oficina.ordemservico.infrastructure.acl.servico.ConsultarServicoAdapter
import br.com.fiap.oficina.ordemservico.infrastructure.acl.servico.ServicoMapper
import br.com.fiap.oficina.ordemservico.infrastructure.acl.veiculo.ConsultarVeiculoAdapter
import br.com.fiap.oficina.ordemservico.infrastructure.acl.veiculo.VeiculoMapper
import br.com.fiap.oficina.ordemservico.infrastructure.mapper.ItemOrcamentoJPAMapper
import br.com.fiap.oficina.ordemservico.infrastructure.mapper.OrdemServicoJPAMapper
import br.com.fiap.oficina.ordemservico.infrastructure.persistence.OrdemServicoJPARepository
import br.com.fiap.oficina.ordemservico.infrastructure.persistence.OrdemServicoRepositoryImpl
import br.com.fiap.oficina.ordemservico.infrastructure.persistence.SequenciaOrdemServicoImpl
import br.com.fiap.oficina.peca.application.port.PecaQueryPort
import br.com.fiap.oficina.servico.application.port.ServicoQueryPort
import br.com.fiap.oficina.veiculo.application.port.VeiculoQueryPort
import jakarta.persistence.EntityManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
internal class OrdemServicoConfig {
    // Mappers
    @Bean
    fun itemOrcamentoJPAMapper(): ItemOrcamentoJPAMapper = ItemOrcamentoJPAMapper()

    @Bean
    fun ordemServicoJPAMapper(itemMapper: ItemOrcamentoJPAMapper): OrdemServicoJPAMapper =
        OrdemServicoJPAMapper(itemMapper)

    @Bean
    fun ordemServicoClienteMapper(): ClienteMapper = ClienteMapper()

    @Bean
    fun ordemServicoFuncionarioMapper(): FuncionarioMapper = FuncionarioMapper()

    @Bean
    fun ordemServicoVeiculoMapper(): VeiculoMapper = VeiculoMapper()

    @Bean
    fun ordemServicoPecaMapper(): PecaMapper = PecaMapper()

    @Bean
    fun ordemServicoServicoMapper(): ServicoMapper = ServicoMapper()

    // Implementations
    @Bean
    fun ordemServicoRepository(
        jpaRepository: OrdemServicoJPARepository,
        mapper: OrdemServicoJPAMapper,
    ): OrdemServicoRepository = OrdemServicoRepositoryImpl(jpaRepository, mapper)

    @Bean
    fun sequenciaOrdemServico(entityManager: EntityManager): SequenciaOrdemServico =
        SequenciaOrdemServicoImpl(entityManager)

    // Anticorruption layer
    @Bean
    fun consultarClienteAdapter(queryPort: ClienteQueryPort, mapper: ClienteMapper): ConsultarCliente =
        ConsultarClienteAdapter(queryPort, mapper)

    @Bean
    fun consultarFuncionarioAdapter(queryPort: FuncionarioQueryPort, mapper: FuncionarioMapper): ConsultarFuncionario =
        ConsultarFuncionarioAdapter(queryPort, mapper)

    @Bean
    fun consultarVeiculoAdapter(queryPort: VeiculoQueryPort, mapper: VeiculoMapper): ConsultarVeiculo =
        ConsultarVeiculoAdapter(queryPort, mapper)

    @Bean
    fun consultarPecaAdapter(queryPort: PecaQueryPort, mapper: PecaMapper): ConsultarPeca =
        ConsultarPecaAdapter(queryPort, mapper)

    @Bean
    fun consultarServicoAdapter(queryPort: ServicoQueryPort, mapper: ServicoMapper): ConsultarServico =
        ConsultarServicoAdapter(queryPort, mapper)
}
