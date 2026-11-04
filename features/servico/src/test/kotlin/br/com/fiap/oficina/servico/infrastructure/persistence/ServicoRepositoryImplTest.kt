package br.com.fiap.oficina.servico.infrastructure.persistence

import br.com.fiap.oficina.servico.ServicoFixtures
import br.com.fiap.oficina.servico.domain.ServicoId
import br.com.fiap.oficina.servico.infrastructure.mapper.ServicoJPAMapper
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.Optional
import java.util.UUID

@DisplayName("Infraestrutura - Repositório de Serviço")
class ServicoRepositoryImplTest {
    private val jpaRepositoryMock = mockk<ServicoJPARepository>()
    private val mapperMock = mockk<ServicoJPAMapper>()
    private val repository = ServicoRepositoryImpl(jpaRepositoryMock, mapperMock)

    @Test
    @DisplayName("Dado um serviço, quando salvar, então deve persistir e retornar o serviço salvo")
    fun givenServico_whenSaving_thenPersistAndReturnSavedServico() {
        val servico = ServicoFixtures.servico()
        val jpa = ServicoFixtures.servicoJPA()
        val jpaSalvo = ServicoFixtures.servicoJPA(descricao = ServicoFixtures.DESCRICAO_ALTERNATIVA)
        val servicoSalvo = ServicoFixtures.servico(descricao = ServicoFixtures.DESCRICAO_ALTERNATIVA)

        every { mapperMock.toJPA(servico) } returns jpa
        every { jpaRepositoryMock.save(jpa) } returns jpaSalvo
        every { mapperMock.toDomain(jpaSalvo) } returns servicoSalvo

        assertSame(servicoSalvo, repository.salvar(servico))

        verify(exactly = 1) { mapperMock.toJPA(servico) }
        verify(exactly = 1) { jpaRepositoryMock.save(jpa) }
        verify(exactly = 1) { mapperMock.toDomain(jpaSalvo) }
    }

    @Test
    @DisplayName("Dado um id existente, quando buscar por id, então deve retornar o serviço convertido")
    fun givenExistingId_whenSearchingById_thenReturnConvertedServico() {
        val id = ServicoId.toUUID(ServicoFixtures.ID_1)
        val jpa = ServicoFixtures.servicoJPA()
        val servico = ServicoFixtures.servico()

        every { jpaRepositoryMock.findById(id.value) } returns Optional.of(jpa)
        every { mapperMock.toDomain(jpa) } returns servico

        assertSame(servico, repository.buscarPorId(id))

        verify(exactly = 1) { jpaRepositoryMock.findById(id.value) }
    }

    @Test
    @DisplayName("Dado um id inexistente, quando buscar por id, então deve retornar nulo")
    fun givenNonExistentId_whenSearchingById_thenReturnNull() {
        val id = ServicoId(UUID.randomUUID())

        every { jpaRepositoryMock.findById(id.value) } returns Optional.empty()

        assertNull(repository.buscarPorId(id))

        verify(exactly = 0) { mapperMock.toDomain(any()) }
    }

    @Test
    @DisplayName("Dado serviços ativos, quando listar ativos, então deve retornar apenas os convertidos")
    fun givenActiveServicos_whenListingActive_thenReturnOnlyConverted() {
        val jpa1 = ServicoFixtures.servicoJPA(id = ServicoFixtures.ID_1)
        val jpa2 = ServicoFixtures.servicoJPA(id = ServicoFixtures.ID_2)
        val servico1 = ServicoFixtures.servico(id = ServicoFixtures.ID_1)
        val servico2 = ServicoFixtures.servico(id = ServicoFixtures.ID_2)

        every { jpaRepositoryMock.findAllByAtivoTrue() } returns listOf(jpa1, jpa2)
        every { mapperMock.toDomain(jpa1) } returns servico1
        every { mapperMock.toDomain(jpa2) } returns servico2

        assertEquals(listOf(servico1, servico2), repository.listarAtivos())

        verify(exactly = 1) { jpaRepositoryMock.findAllByAtivoTrue() }
        verify(exactly = 0) { jpaRepositoryMock.findAll() }
    }

    @Test
    @DisplayName("Dado nenhum serviço ativo, quando listar ativos, então deve retornar lista vazia")
    fun givenNoActiveServico_whenListingActive_thenReturnEmptyList() {
        every { jpaRepositoryMock.findAllByAtivoTrue() } returns emptyList()

        assertTrue(repository.listarAtivos().isEmpty())

        verify(exactly = 0) { mapperMock.toDomain(any()) }
    }

    @Test
    @DisplayName("Dado serviços cadastrados, quando listar todos, então deve retornar todos convertidos")
    fun givenRegisteredServicos_whenListingAll_thenReturnAllConverted() {
        val jpaAtivo = ServicoFixtures.servicoJPA(id = ServicoFixtures.ID_1, ativo = true)
        val jpaInativo = ServicoFixtures.servicoJPA(id = ServicoFixtures.ID_2, ativo = false)
        val servicoAtivo = ServicoFixtures.servico(id = ServicoFixtures.ID_1, ativo = true)
        val servicoInativo = ServicoFixtures.servico(id = ServicoFixtures.ID_2, ativo = false)

        every { jpaRepositoryMock.findAll() } returns listOf(jpaAtivo, jpaInativo)
        every { mapperMock.toDomain(jpaAtivo) } returns servicoAtivo
        every { mapperMock.toDomain(jpaInativo) } returns servicoInativo

        assertEquals(listOf(servicoAtivo, servicoInativo), repository.listarTodos())

        verify(exactly = 1) { jpaRepositoryMock.findAll() }
        verify(exactly = 2) { mapperMock.toDomain(any()) }
    }

    @Test
    @DisplayName("Dado nenhum serviço cadastrado, quando listar todos, então deve retornar lista vazia")
    fun givenNoServico_whenListingAll_thenReturnEmptyList() {
        every { jpaRepositoryMock.findAll() } returns emptyList()

        assertTrue(repository.listarTodos().isEmpty())

        verify(exactly = 0) { mapperMock.toDomain(any()) }
    }
}
