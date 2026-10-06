package br.com.fiap.oficina.funcionario.infrastructure.persistence

import br.com.fiap.oficina.funcionario.domain.Cargo
import br.com.fiap.oficina.funcionario.domain.Funcionario
import br.com.fiap.oficina.funcionario.domain.FuncionarioId
import br.com.fiap.oficina.funcionario.infrastructure.mapper.FuncionarioJPAMapper
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.Optional
import java.util.UUID

@DisplayName("Infraestrutura - Repositório de Funcionário")
class FuncionarioRepositoryImplTest {
    private val jpaRepositoryMock = mockk<FuncionarioJPARepository>()
    private val mapperMock = mockk<FuncionarioJPAMapper>()
    private val repository = FuncionarioRepositoryImpl(jpaRepositoryMock, mapperMock)

    @Test
    @DisplayName("Dado um funcionário, quando salvar, então deve persistir e retornar o funcionário salvo")
    fun givenFuncionario_whenSaving_thenPersistAndReturnSavedFuncionario() {
        val funcionario = funcionario()
        val jpa = funcionarioJPA()
        val jpaSalvo = jpa.copy(nome = "Nome persistido")
        val funcionarioSalvo = funcionario(nome = "Nome persistido")

        every { mapperMock.toJPA(funcionario) } returns jpa
        every { jpaRepositoryMock.save(jpa) } returns jpaSalvo
        every { mapperMock.toDomain(jpaSalvo) } returns funcionarioSalvo

        val resultado = repository.salvar(funcionario)

        assertSame(funcionarioSalvo, resultado)

        verify(exactly = 1) { mapperMock.toJPA(funcionario) }
        verify(exactly = 1) { jpaRepositoryMock.save(jpa) }
        verify(exactly = 1) { mapperMock.toDomain(jpaSalvo) }
    }

    @Test
    @DisplayName("Dado funcionários cadastrados, quando listar todos, então deve retornar todos convertidos")
    fun givenRegisteredFuncionarios_whenListingAll_thenReturnAllConverted() {
        val jpa1 = funcionarioJPA(id = ID_1, cpf = "01234567890")
        val jpa2 = funcionarioJPA(id = ID_2, cpf = "01234567891")
        val funcionario1 = funcionario(id = ID_1, cpf = "01234567890")
        val funcionario2 = funcionario(id = ID_2, cpf = "01234567891")

        every { jpaRepositoryMock.findAll() } returns listOf(jpa1, jpa2)
        every { mapperMock.toDomain(jpa1) } returns funcionario1
        every { mapperMock.toDomain(jpa2) } returns funcionario2

        val resultado = repository.listarTodos()

        assertEquals(listOf(funcionario1, funcionario2), resultado)

        verify(exactly = 1) { jpaRepositoryMock.findAll() }
        verify(exactly = 2) { mapperMock.toDomain(any()) }
    }

    @Test
    @DisplayName("Dado nenhum funcionário cadastrado, quando listar todos, então deve retornar lista vazia")
    fun givenNoFuncionarios_whenListingAll_thenReturnEmptyList() {
        every { jpaRepositoryMock.findAll() } returns emptyList()

        val resultado = repository.listarTodos()

        assertTrue(resultado.isEmpty())

        verify(exactly = 0) { mapperMock.toDomain(any()) }
    }

    @Test
    @DisplayName("Dado um id existente, quando buscar por id, então deve retornar o funcionário")
    fun givenExistingId_whenFindingById_thenReturnFuncionario() {
        val jpa = funcionarioJPA()
        val funcionario = funcionario()

        every { jpaRepositoryMock.findById(UUID.fromString(ID_1)) } returns Optional.of(jpa)
        every { mapperMock.toDomain(jpa) } returns funcionario

        val resultado = repository.buscarPorId(FuncionarioId.toUUID(ID_1))

        assertSame(funcionario, resultado)

        verify(exactly = 1) { jpaRepositoryMock.findById(UUID.fromString(ID_1)) }
    }

    @Test
    @DisplayName("Dado um id inexistente, quando buscar por id, então deve retornar nulo")
    fun givenNonExistentId_whenFindingById_thenReturnNull() {
        every { jpaRepositoryMock.findById(UUID.fromString(ID_1)) } returns Optional.empty()

        val resultado = repository.buscarPorId(FuncionarioId.toUUID(ID_1))

        assertNull(resultado)

        verify(exactly = 0) { mapperMock.toDomain(any()) }
    }

    @Test
    @DisplayName("Dado um cpf existente, quando buscar por cpf, então deve retornar o funcionário")
    fun givenExistingCpf_whenFindingByCpf_thenReturnFuncionario() {
        val jpa = funcionarioJPA()
        val funcionario = funcionario()

        every { jpaRepositoryMock.findByCpf(CPF) } returns jpa
        every { mapperMock.toDomain(jpa) } returns funcionario

        val resultado = repository.buscarPorCpf(CPF)

        assertSame(funcionario, resultado)

        verify(exactly = 1) { jpaRepositoryMock.findByCpf(CPF) }
    }

    @Test
    @DisplayName("Dado um cpf inexistente, quando buscar por cpf, então deve retornar nulo")
    fun givenNonExistentCpf_whenFindingByCpf_thenReturnNull() {
        every { jpaRepositoryMock.findByCpf(CPF) } returns null

        val resultado = repository.buscarPorCpf(CPF)

        assertNull(resultado)

        verify(exactly = 0) { mapperMock.toDomain(any()) }
    }

    @Test
    @DisplayName("Dado um nome existente, quando buscar por nome, então deve retornar o funcionário")
    fun givenExistingNome_whenFindingByNome_thenReturnFuncionario() {
        val jpa = funcionarioJPA()
        val funcionario = funcionario()

        every { jpaRepositoryMock.findByNome(NOME) } returns jpa
        every { mapperMock.toDomain(jpa) } returns funcionario

        val resultado = repository.buscarPorNome(NOME)

        assertSame(funcionario, resultado)

        verify(exactly = 1) { jpaRepositoryMock.findByNome(NOME) }
    }

    @Test
    @DisplayName("Dado um nome inexistente, quando buscar por nome, então deve retornar nulo")
    fun givenNonExistentNome_whenFindingByNome_thenReturnNull() {
        every { jpaRepositoryMock.findByNome(NOME) } returns null

        val resultado = repository.buscarPorNome(NOME)

        assertNull(resultado)

        verify(exactly = 0) { mapperMock.toDomain(any()) }
    }

    @Test
    @DisplayName("Dado um id, quando deletar, então deve remover pelo uuid")
    fun givenId_whenDeleting_thenDeleteByUuid() {
        every { jpaRepositoryMock.deleteById(UUID.fromString(ID_1)) } just runs

        repository.deletar(FuncionarioId.toUUID(ID_1))

        verify(exactly = 1) { jpaRepositoryMock.deleteById(UUID.fromString(ID_1)) }
    }

    private fun funcionario(id: String = ID_1, nome: String = NOME, cpf: String = CPF) =
        Funcionario.reconstruir(id = id, nome = nome, cpf = cpf, cargo = Cargo.MECANICO.descricao)

    private fun funcionarioJPA(id: String = ID_1, cpf: String = CPF) =
        FuncionarioJPA(id = UUID.fromString(id), nome = NOME, cargo = Cargo.MECANICO, cpf = cpf)

    private companion object {
        const val ID_1 = "00000000-0000-0000-0000-000000000100"
        const val ID_2 = "00000000-0000-0000-0000-000000000200"
        const val NOME = "João Silva"
        const val CPF = "01234567890"
    }
}
