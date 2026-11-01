package br.com.fiap.oficina.peca.infrastructure.config

import br.com.fiap.oficina.peca.infrastructure.persistence.PecaJPARepository
import br.com.fiap.oficina.peca.infrastructure.persistence.PecaRepositoryImpl
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Infraestrutura - Configuração de Peça")
class PecaConfigTest {
    private val config = PecaConfig()

    @Test
    @DisplayName("Dado a configuração, quando criar o mapper, então deve retornar uma nova instância")
    fun givenConfig_whenCreatingMapper_thenReturnNewInstance() {
        assertNotSame(config.pecaJPAMapper(), config.pecaJPAMapper())
    }

    @Test
    @DisplayName("Dado as dependências, quando criar o repositório, então deve delegar ao repositório JPA")
    fun givenDependencies_whenCreatingRepository_thenDelegateToJpaRepository() {
        val jpaRepositoryMock = mockk<PecaJPARepository>()
        every { jpaRepositoryMock.findAll() } returns emptyList()

        val repository = config.pecaRepository(jpaRepositoryMock, config.pecaJPAMapper())

        assertInstanceOf(PecaRepositoryImpl::class.java, repository)

        repository.listar()

        verify(exactly = 1) { jpaRepositoryMock.findAll() }
    }
}
