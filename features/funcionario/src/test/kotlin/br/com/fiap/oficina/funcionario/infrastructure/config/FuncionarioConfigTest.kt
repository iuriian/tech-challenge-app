package br.com.fiap.oficina.funcionario.infrastructure.config

import br.com.fiap.oficina.funcionario.infrastructure.persistence.FuncionarioJPARepository
import br.com.fiap.oficina.funcionario.infrastructure.persistence.FuncionarioRepositoryImpl
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Infraestrutura - Configuração de Funcionário")
class FuncionarioConfigTest {
    private val config = FuncionarioConfig()

    @Test
    @DisplayName("Dado a configuração, quando criar o mapper, então deve retornar uma nova instância")
    fun givenConfig_whenCreatingMapper_thenReturnNewInstance() {
        assertNotSame(config.mapper(), config.mapper())
    }

    @Test
    @DisplayName("Dado as dependências, quando criar o repositório, então deve delegar ao repositório JPA")
    fun givenDependencies_whenCreatingRepository_thenDelegateToJpaRepository() {
        val jpaRepositoryMock = mockk<FuncionarioJPARepository>()
        every { jpaRepositoryMock.findAll() } returns emptyList()

        val repository = config.funcionarioRepository(jpaRepositoryMock, config.mapper())

        assertInstanceOf(FuncionarioRepositoryImpl::class.java, repository)

        repository.listarTodos()

        verify(exactly = 1) { jpaRepositoryMock.findAll() }
    }
}
