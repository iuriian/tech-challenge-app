package br.com.fiap.oficina.peca.infrastructure.config

import br.com.fiap.oficina.peca.domain.PecaRepository
import br.com.fiap.oficina.peca.infrastructure.mapper.PecaJPAMapper
import br.com.fiap.oficina.peca.infrastructure.persistence.PecaJPARepository
import br.com.fiap.oficina.peca.infrastructure.persistence.PecaRepositoryImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
internal class PecaConfig {
    @Bean
    fun pecaJPAMapper(): PecaJPAMapper = PecaJPAMapper()

    @Bean
    fun pecaRepository(
        jpaRepository: PecaJPARepository,
        mapper: PecaJPAMapper,
    ): PecaRepository = PecaRepositoryImpl(jpaRepository, mapper)
}
