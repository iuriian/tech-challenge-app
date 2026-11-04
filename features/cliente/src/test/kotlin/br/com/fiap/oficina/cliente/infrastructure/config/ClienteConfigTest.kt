package br.com.fiap.oficina.cliente.infrastructure.config

import br.com.fiap.oficina.cliente.infrastructure.persistence.ClienteJPARepository
import br.com.fiap.oficina.cliente.infrastructure.persistence.ClienteRepositoryImpl
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Infraestrutura - Configuração de Cliente")
class ClienteConfigTest {
    private val config = ClienteConfig()

    @Test
    @DisplayName("Dado a configuração, quando criar os mappers, então deve retornar novas instâncias")
    fun givenConfig_whenCreatingMappers_thenReturnNewInstances() {
        assertNotSame(config.clienteJpaMapper(), config.clienteJpaMapper())
        assertNotSame(config.enderecoJpaMapper(), config.enderecoJpaMapper())
        assertNotSame(config.contatoJpaMapper(), config.contatoJpaMapper())
    }

    @Test
    @DisplayName("Dado as dependências, quando criar o repositório, então deve delegar ao repositório JPA")
    fun givenDependencies_whenCreatingRepository_thenDelegateToJpaRepository() {
        val jpaRepositoryMock = mockk<ClienteJPARepository>()
        every { jpaRepositoryMock.findAll() } returns emptyList()

        val repository = config.clienteRepository(jpaRepositoryMock, config.clienteJpaMapper())

        assertInstanceOf(ClienteRepositoryImpl::class.java, repository)

        repository.listarTodos()

        verify(exactly = 1) { jpaRepositoryMock.findAll() }
    }
}
