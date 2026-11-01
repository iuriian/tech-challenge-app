package br.com.fiap.oficina.veiculo.infrastructure.persistence

import br.com.fiap.oficina.shared.domain.exception.BusinessRuleException
import br.com.fiap.oficina.shared.domain.valueobject.Placa
import br.com.fiap.oficina.veiculo.VeiculoFixtures
import br.com.fiap.oficina.veiculo.domain.ProprietarioRef
import br.com.fiap.oficina.veiculo.domain.VeiculoId
import br.com.fiap.oficina.veiculo.infrastructure.mapper.VeiculoJPAMapper
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.dao.DataIntegrityViolationException
import java.util.Optional
import java.util.UUID
import kotlin.test.assertFailsWith

@DisplayName("Infraestrutura - Repositório de Veículo")
class VeiculoRepositoryImplTest {
    private val jpaRepositoryMock = mockk<VeiculoJPARepository>()
    private val mapperMock = mockk<VeiculoJPAMapper>()
    private val repository = VeiculoRepositoryImpl(jpaRepositoryMock, mapperMock)

    @Test
    @DisplayName("Dado um veículo, quando salvar, então deve persistir e retornar o veículo salvo")
    fun givenVeiculo_whenSaving_thenPersistAndReturnSavedVeiculo() {
        val veiculo = VeiculoFixtures.veiculo()
        val jpa = VeiculoFixtures.veiculoJPA()
        val jpaSalvo = jpa.copy(marca = "Marca persistida")
        val veiculoSalvo = VeiculoFixtures.veiculo(marca = "Marca persistida")

        every { mapperMock.toJPA(veiculo) } returns jpa
        every { jpaRepositoryMock.saveAndFlush(jpa) } returns jpaSalvo
        every { mapperMock.toDomain(jpaSalvo) } returns veiculoSalvo

        assertSame(veiculoSalvo, repository.salvar(veiculo))

        verify(exactly = 1) { mapperMock.toJPA(veiculo) }
        verify(exactly = 1) { jpaRepositoryMock.saveAndFlush(jpa) }
        verify(exactly = 1) { mapperMock.toDomain(jpaSalvo) }
    }

    @Test
    @DisplayName("Dado uma violação de integridade, quando salvar, então deve lançar exceção de regra de negócio")
    fun givenIntegrityViolation_whenSaving_thenThrowBusinessRuleException() {
        val veiculo = VeiculoFixtures.veiculo()
        val jpa = VeiculoFixtures.veiculoJPA()
        val causa = DataIntegrityViolationException("placa duplicada")

        every { mapperMock.toJPA(veiculo) } returns jpa
        every { jpaRepositoryMock.saveAndFlush(jpa) } throws causa

        val exception = assertFailsWith<BusinessRuleException> { repository.salvar(veiculo) }

        assertEquals("Já existe veículo cadastrado com a placa ${VeiculoFixtures.PLACA}", exception.message)
        assertSame(causa, exception.cause)

        verify(exactly = 0) { mapperMock.toDomain(any()) }
    }

    @Test
    @DisplayName("Dado veículos cadastrados, quando listar todos, então deve retornar todos convertidos")
    fun givenRegisteredVeiculos_whenListingAll_thenReturnAllConverted() {
        val jpa1 = VeiculoFixtures.veiculoJPA()
        val jpa2 = VeiculoFixtures.veiculoJPA(id = VeiculoFixtures.ID_2, placa = VeiculoFixtures.PLACA_OUTRA)
        val veiculo1 = VeiculoFixtures.veiculo()
        val veiculo2 = VeiculoFixtures.veiculo(id = VeiculoFixtures.ID_2, placa = VeiculoFixtures.PLACA_OUTRA)

        every { jpaRepositoryMock.findAll() } returns listOf(jpa1, jpa2)
        every { mapperMock.toDomain(jpa1) } returns veiculo1
        every { mapperMock.toDomain(jpa2) } returns veiculo2

        assertEquals(listOf(veiculo1, veiculo2), repository.listarTodos())

        verify(exactly = 1) { jpaRepositoryMock.findAll() }
        verify(exactly = 2) { mapperMock.toDomain(any()) }
    }

    @Test
    @DisplayName("Dado nenhum veículo cadastrado, quando listar todos, então deve retornar lista vazia")
    fun givenNoVeiculos_whenListingAll_thenReturnEmptyList() {
        every { jpaRepositoryMock.findAll() } returns emptyList()

        assertTrue(repository.listarTodos().isEmpty())

        verify(exactly = 0) { mapperMock.toDomain(any()) }
    }

    @Test
    @DisplayName("Dado um id existente, quando buscar por id, então deve retornar o veículo convertido")
    fun givenExistingId_whenSearchingById_thenReturnConvertedVeiculo() {
        val id = VeiculoId.toUUID(VeiculoFixtures.ID_1)
        val jpa = VeiculoFixtures.veiculoJPA()
        val veiculo = VeiculoFixtures.veiculo()

        every { jpaRepositoryMock.findById(id.value) } returns Optional.of(jpa)
        every { mapperMock.toDomain(jpa) } returns veiculo

        assertSame(veiculo, repository.buscarPorId(id))

        verify(exactly = 1) { jpaRepositoryMock.findById(id.value) }
    }

    @Test
    @DisplayName("Dado um id inexistente, quando buscar por id, então deve retornar nulo")
    fun givenNonExistentId_whenSearchingById_thenReturnNull() {
        every { jpaRepositoryMock.findById(any<UUID>()) } returns Optional.empty()

        assertNull(repository.buscarPorId(VeiculoId.toUUID(VeiculoFixtures.ID_1)))

        verify(exactly = 0) { mapperMock.toDomain(any()) }
    }

    @Test
    @DisplayName("Dado uma placa cadastrada, quando buscar por placa, então deve retornar o veículo convertido")
    fun givenRegisteredPlaca_whenSearchingByPlaca_thenReturnConvertedVeiculo() {
        val jpa = VeiculoFixtures.veiculoJPA()
        val veiculo = VeiculoFixtures.veiculo()

        every { jpaRepositoryMock.findByPlaca(VeiculoFixtures.PLACA) } returns jpa
        every { mapperMock.toDomain(jpa) } returns veiculo

        assertSame(veiculo, repository.buscarPorPlaca(Placa.criar(VeiculoFixtures.PLACA)))

        verify(exactly = 1) { jpaRepositoryMock.findByPlaca(VeiculoFixtures.PLACA) }
    }

    @Test
    @DisplayName("Dado uma placa não cadastrada, quando buscar por placa, então deve retornar nulo")
    fun givenUnregisteredPlaca_whenSearchingByPlaca_thenReturnNull() {
        every { jpaRepositoryMock.findByPlaca(any()) } returns null

        assertNull(repository.buscarPorPlaca(Placa.criar(VeiculoFixtures.PLACA)))

        verify(exactly = 0) { mapperMock.toDomain(any()) }
    }

    @Test
    @DisplayName("Dado um proprietário com veículos, quando buscar por proprietário, então deve retornar convertidos")
    fun givenProprietarioWithVeiculos_whenSearchingByProprietario_thenReturnConverted() {
        val ref = ProprietarioRef.toUUID(VeiculoFixtures.PROPRIETARIO_ID)
        val jpa = VeiculoFixtures.veiculoJPA()
        val veiculo = VeiculoFixtures.veiculo()

        every { jpaRepositoryMock.findByProprietarioId(ref.value) } returns listOf(jpa)
        every { mapperMock.toDomain(jpa) } returns veiculo

        assertEquals(listOf(veiculo), repository.buscarPorProprietario(ref))

        verify(exactly = 1) { jpaRepositoryMock.findByProprietarioId(ref.value) }
    }

    @Test
    @DisplayName("Dado um proprietário sem veículos, quando buscar por proprietário, então deve retornar lista vazia")
    fun givenProprietarioWithoutVeiculos_whenSearchingByProprietario_thenReturnEmptyList() {
        every { jpaRepositoryMock.findByProprietarioId(any()) } returns emptyList()

        assertTrue(repository.buscarPorProprietario(ProprietarioRef.toUUID(VeiculoFixtures.PROPRIETARIO_ID)).isEmpty())

        verify(exactly = 0) { mapperMock.toDomain(any()) }
    }

    @Test
    @DisplayName("Dado uma placa, quando verificar a existência, então deve delegar ao repositório JPA")
    fun givenPlaca_whenCheckingExistence_thenDelegateToJpaRepository() {
        every { jpaRepositoryMock.existsByPlaca(VeiculoFixtures.PLACA) } returns true
        every { jpaRepositoryMock.existsByPlaca(VeiculoFixtures.PLACA_OUTRA) } returns false

        assertTrue(repository.existeVeiculo(Placa.criar(VeiculoFixtures.PLACA)))
        assertFalse(repository.existeVeiculo(Placa.criar(VeiculoFixtures.PLACA_OUTRA)))

        verify(exactly = 1) { jpaRepositoryMock.existsByPlaca(VeiculoFixtures.PLACA) }
        verify(exactly = 1) { jpaRepositoryMock.existsByPlaca(VeiculoFixtures.PLACA_OUTRA) }
    }

    @Test
    @DisplayName("Dado um id, quando deletar, então deve delegar ao repositório JPA")
    fun givenId_whenDeleting_thenDelegateToJpaRepository() {
        val id = VeiculoId.toUUID(VeiculoFixtures.ID_1)

        every { jpaRepositoryMock.deleteById(id.value) } just runs

        repository.deletar(id)

        verify(exactly = 1) { jpaRepositoryMock.deleteById(id.value) }
    }
}
