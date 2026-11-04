package br.com.fiap.oficina.servico.infrastructure.config

import br.com.fiap.oficina.servico.domain.ServicoRepository
import br.com.fiap.oficina.servico.infrastructure.mapper.ServicoJPAMapper
import br.com.fiap.oficina.servico.infrastructure.persistence.ServicoJPARepository
import br.com.fiap.oficina.servico.infrastructure.persistence.ServicoRepositoryImpl
import br.com.fiap.oficina.servico.infrastructure.provider.ServicoQueryMapper
import br.com.fiap.oficina.servico.infrastructure.provider.ServicoQueryProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
internal class ServicoConfig {
    @Bean
    fun servicoJPAMapper(): ServicoJPAMapper = ServicoJPAMapper()

    @Bean
    fun servicoRepository(jpaRepository: ServicoJPARepository, mapper: ServicoJPAMapper): ServicoRepository =
        ServicoRepositoryImpl(jpaRepository, mapper)

    @Bean
    fun servicoQueryMapper(): ServicoQueryMapper = ServicoQueryMapper()

    @Bean
    fun servicoQueryProvider(repository: ServicoRepository, mapper: ServicoQueryMapper): ServicoQueryProvider =
        ServicoQueryProvider(repository, mapper)
}
