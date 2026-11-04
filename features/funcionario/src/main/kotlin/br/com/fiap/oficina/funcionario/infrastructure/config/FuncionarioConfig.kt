package br.com.fiap.oficina.funcionario.infrastructure.config

import br.com.fiap.oficina.funcionario.domain.FuncionarioRepository
import br.com.fiap.oficina.funcionario.infrastructure.mapper.FuncionarioJPAMapper
import br.com.fiap.oficina.funcionario.infrastructure.persistence.FuncionarioJPARepository
import br.com.fiap.oficina.funcionario.infrastructure.persistence.FuncionarioRepositoryImpl
import br.com.fiap.oficina.funcionario.infrastructure.provider.FuncionarioQueryMapper
import br.com.fiap.oficina.funcionario.infrastructure.provider.FuncionarioQueryProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
internal class FuncionarioConfig {
    @Bean
    fun funcionarioJPAMapper(): FuncionarioJPAMapper = FuncionarioJPAMapper()

    @Bean
    fun funcionarioRepository(
        jpaRepository: FuncionarioJPARepository,
        mapper: FuncionarioJPAMapper,
    ): FuncionarioRepository = FuncionarioRepositoryImpl(jpaRepository, mapper)

    @Bean
    fun funcionarioQueryMapper(): FuncionarioQueryMapper = FuncionarioQueryMapper()

    @Bean
    fun funcionarioQueryProvider(
        repository: FuncionarioRepository,
        mapper: FuncionarioQueryMapper,
    ): FuncionarioQueryProvider = FuncionarioQueryProvider(repository, mapper)
}
