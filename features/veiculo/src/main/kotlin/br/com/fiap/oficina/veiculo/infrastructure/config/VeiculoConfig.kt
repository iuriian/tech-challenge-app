package br.com.fiap.oficina.veiculo.infrastructure.config

import br.com.fiap.oficina.veiculo.domain.VeiculoRepository
import br.com.fiap.oficina.veiculo.infrastructure.mapper.VeiculoJPAMapper
import br.com.fiap.oficina.veiculo.infrastructure.persistence.VeiculoJPARepository
import br.com.fiap.oficina.veiculo.infrastructure.persistence.VeiculoRepositoryImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
internal class VeiculoConfig {
    @Bean
    fun veiculoJpaMapper() = VeiculoJPAMapper()

    @Bean
    fun veiculoRepository(
        repository: VeiculoJPARepository,
        jpaMapper: VeiculoJPAMapper,
    ): VeiculoRepository = VeiculoRepositoryImpl(repository, jpaMapper)
}
