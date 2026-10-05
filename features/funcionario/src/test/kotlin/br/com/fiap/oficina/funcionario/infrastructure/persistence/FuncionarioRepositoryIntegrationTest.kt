package br.com.fiap.oficina.funcionario.infrastructure.persistence

import br.com.fiap.oficina.funcionario.domain.Cargo
import br.com.fiap.oficina.funcionario.domain.Funcionario
import br.com.fiap.oficina.funcionario.domain.FuncionarioId
import br.com.fiap.oficina.funcionario.domain.FuncionarioRepository
import br.com.fiap.oficina.funcionario.infrastructure.config.FuncionarioConfig
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Import
import org.springframework.dao.DataIntegrityViolationException
import org.testcontainers.containers.PostgreSQLContainer
import kotlin.test.assertFailsWith

@DataJpaTest(properties = ["spring.jpa.hibernate.ddl-auto=create-drop"])
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(FuncionarioConfig::class)
@DisplayName("Infraestrutura - Repositório de Funcionário (integração)")
class FuncionarioRepositoryIntegrationTest {
    @Autowired
    private lateinit var repository: FuncionarioRepository

    @Autowired
    private lateinit var jpaRepository: FuncionarioJPARepository

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Test
    @DisplayName("Dado um novo funcionário, quando salvar, então deve persistir e ser encontrado por id")
    fun givenNewFuncionario_whenSaving_thenPersistAndFindById() {
        val funcionario = Funcionario.criar(nome = "João Silva", cpf = CPF_1, cargo = "Mecânico")

        val salvo = repository.salvar(funcionario)
        sincronizar()

        assertEquals(funcionario, salvo)
        assertEquals(funcionario, repository.buscarPorId(funcionario.id))
    }

    @Test
    @DisplayName("Dado um funcionário existente, quando salvar alterações, então deve atualizar o registro")
    fun givenExistingFuncionario_whenSavingChanges_thenUpdateRecord() {
        val funcionario = repository.salvar(Funcionario.criar(nome = "João", cpf = CPF_1, cargo = "Atendente"))
        sincronizar()

        repository.salvar(funcionario.atualizar(nome = "João Silva", cpf = CPF_2, cargo = "Mecânico"))
        sincronizar()

        val atualizado = repository.buscarPorId(funcionario.id)

        assertEquals(1, jpaRepository.count())
        assertEquals("João Silva", atualizado?.nome)
        assertEquals(CPF_2, atualizado?.cpf?.value)
        assertEquals("Mecânico", atualizado?.cargo?.descricao)
    }

    @Test
    @DisplayName("Dado um funcionário, quando salvar, então deve persistir o cargo pelo nome do enum")
    fun givenFuncionario_whenSaving_thenPersistCargoAsEnumName() {
        val funcionario = repository.salvar(Funcionario.criar(nome = "João", cpf = CPF_1, cargo = "Mecânico"))
        sincronizar()

        val cargo =
            entityManager.entityManager
                .createNativeQuery("SELECT cargo FROM funcionarios WHERE id = :id")
                .setParameter("id", funcionario.id.value)
                .singleResult

        assertEquals("MECANICO", cargo)
    }

    @Test
    @DisplayName("Dado funcionários cadastrados, quando listar todos, então deve retornar todos")
    fun givenRegisteredFuncionarios_whenListingAll_thenReturnAll() {
        val funcionarios =
            listOf(
                Funcionario.criar(nome = "João", cpf = CPF_1, cargo = "Mecânico"),
                Funcionario.criar(nome = "Maria", cpf = CPF_2, cargo = "Atendente"),
            )
        funcionarios.forEach(repository::salvar)
        sincronizar()

        val resultado = repository.listarTodos()

        assertEquals(funcionarios.toSet(), resultado.toSet())
    }

    @Test
    @DisplayName("Dado nenhum funcionário cadastrado, quando listar todos, então deve retornar lista vazia")
    fun givenNoFuncionarios_whenListingAll_thenReturnEmptyList() {
        assertEquals(emptyList<Funcionario>(), repository.listarTodos())
    }

    @Test
    @DisplayName("Dado um id inexistente, quando buscar por id, então deve retornar nulo")
    fun givenNonExistentId_whenFindingById_thenReturnNull() {
        assertNull(repository.buscarPorId(FuncionarioId.generate()))
    }

    @Test
    @DisplayName("Dado um cpf cadastrado, quando buscar por cpf, então deve retornar o funcionário")
    fun givenRegisteredCpf_whenFindingByCpf_thenReturnFuncionario() {
        val funcionario = repository.salvar(Funcionario.criar(nome = "João", cpf = CPF_1, cargo = "Mecânico"))
        sincronizar()

        assertEquals(funcionario, repository.buscarPorCpf(CPF_1))
        assertNull(repository.buscarPorCpf(CPF_2))
    }

    @Test
    @DisplayName("Dado um nome cadastrado, quando buscar por nome, então deve retornar o funcionário")
    fun givenRegisteredNome_whenFindingByNome_thenReturnFuncionario() {
        val funcionario = repository.salvar(Funcionario.criar(nome = "João", cpf = CPF_1, cargo = "Mecânico"))
        sincronizar()

        assertEquals(funcionario, repository.buscarPorNome("João"))
        assertNull(repository.buscarPorNome("Maria"))
    }

    @Test
    @DisplayName("Dado um funcionário cadastrado, quando deletar, então deve remover o registro")
    fun givenRegisteredFuncionario_whenDeleting_thenRemoveRecord() {
        val funcionario = repository.salvar(Funcionario.criar(nome = "João", cpf = CPF_1, cargo = "Mecânico"))
        sincronizar()

        repository.deletar(funcionario.id)
        sincronizar()

        assertNull(repository.buscarPorId(funcionario.id))
        assertEquals(0, jpaRepository.count())
    }

    @Test
    @DisplayName("Dado um id inexistente, quando deletar, então não deve lançar exceção")
    fun givenNonExistentId_whenDeleting_thenNotThrowException() {
        repository.deletar(FuncionarioId.generate())
        sincronizar()

        assertEquals(0, jpaRepository.count())
    }

    @Test
    @DisplayName("Dado um cpf já cadastrado para outro funcionário, quando salvar, então deve violar a unicidade")
    fun givenCpfAlreadyRegistered_whenSaving_thenViolateUniqueConstraint() {
        repository.salvar(Funcionario.criar(nome = "João", cpf = CPF_1, cargo = "Mecânico"))
        sincronizar()

        assertFailsWith<DataIntegrityViolationException> {
            jpaRepository.saveAndFlush(novoFuncionarioJPA(nome = "Maria", cpf = CPF_1))
        }
    }

    @Test
    @DisplayName("Dado um nome acima de 100 caracteres, quando salvar, então deve violar o tamanho da coluna")
    fun givenNomeLongerThanColumn_whenSaving_thenViolateColumnLength() {
        assertFailsWith<DataIntegrityViolationException> {
            jpaRepository.saveAndFlush(novoFuncionarioJPA(nome = "a".repeat(101), cpf = CPF_1))
        }
    }

    private fun sincronizar() {
        entityManager.flush()
        entityManager.clear()
    }

    private fun novoFuncionarioJPA(
        nome: String,
        cpf: String,
    ) = FuncionarioJPA(id = FuncionarioId.generate().value, nome = nome, cargo = Cargo.ATENDENTE, cpf = cpf)

    companion object {
        private const val CPF_1 = "01234567890"
        private const val CPF_2 = "01234567891"

        @JvmStatic
        @ServiceConnection
        val postgres: PostgreSQLContainer<*> =
            PostgreSQLContainer("postgres:16-alpine")
    }
}
