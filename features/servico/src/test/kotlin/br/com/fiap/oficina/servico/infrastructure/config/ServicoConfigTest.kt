package br.com.fiap.oficina.servico.infrastructure.config

import br.com.fiap.oficina.servico.infrastructure.persistence.ServicoJPARepository
import br.com.fiap.oficina.servico.infrastructure.persistence.ServicoRepositoryImpl
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Infraestrutura - Configuração de Serviço")
class ServicoConfigTest {
    private val config = ServicoConfig()

    @Test
    @DisplayName("Dado a configuração, quando criar o mapper, então deve retornar uma nova instância")
    fun givenConfig_whenCreatingMapper_thenReturnNewInstance() {
        assertNotSame(config.servicoJPAMapper(), config.servicoJPAMapper())
    }

    @Test
    @DisplayName("Dado as dependências, quando criar o repositório, então deve delegar ao repositório JPA")
    fun givenDependencies_whenCreatingRepository_thenDelegateToJpaRepository() {
        val jpaRepositoryMock = mockk<ServicoJPARepository>()
        every { jpaRepositoryMock.findAll() } returns emptyList()

        val repository = config.servicoRepository(jpaRepositoryMock, config.servicoJPAMapper())

        assertInstanceOf(ServicoRepositoryImpl::class.java, repository)

        repository.listarTodos()

        verify(exactly = 1) { jpaRepositoryMock.findAll() }
    }

    @Test
    @DisplayName("Dado o repositório criado, quando listar ativos, então deve delegar a consulta por ativos")
    fun givenCreatedRepository_whenListingActive_thenDelegateActiveQuery() {
        val jpaRepositoryMock = mockk<ServicoJPARepository>()
        every { jpaRepositoryMock.findAllByAtivoTrue() } returns emptyList()

        config.servicoRepository(jpaRepositoryMock, config.servicoJPAMapper()).listarAtivos()

        verify(exactly = 1) { jpaRepositoryMock.findAllByAtivoTrue() }
    }
}
