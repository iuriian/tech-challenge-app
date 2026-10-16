package br.com.fiap.oficina.veiculo.infrastructure.config

import br.com.fiap.oficina.cliente.application.port.ClienteQueryPort
import br.com.fiap.oficina.veiculo.domain.VeiculoRepository
import br.com.fiap.oficina.veiculo.infrastructure.acl.ConsultarProprietarioAdapter
import br.com.fiap.oficina.veiculo.infrastructure.acl.ProprietarioMapper
import br.com.fiap.oficina.veiculo.infrastructure.mapper.VeiculoJPAMapper
import br.com.fiap.oficina.veiculo.infrastructure.persistence.VeiculoJPARepository
import br.com.fiap.oficina.veiculo.infrastructure.persistence.VeiculoRepositoryImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
internal class VeiculoConfig {
    // Mappers
    @Bean
    fun veiculoJpaMapper() = VeiculoJPAMapper()

    @Bean
    fun proprietarioMapper() = ProprietarioMapper()

    // Implementations
    @Bean
    fun veiculoRepository(
        repository: VeiculoJPARepository,
        jpaMapper: VeiculoJPAMapper,
    ): VeiculoRepository = VeiculoRepositoryImpl(repository, jpaMapper)

    @Bean
    fun consultarProprietarioAdapter(
        queryPort: ClienteQueryPort,
        mapper: ProprietarioMapper,
    ): ConsultarProprietarioAdapter = ConsultarProprietarioAdapter(queryPort, mapper)
}
