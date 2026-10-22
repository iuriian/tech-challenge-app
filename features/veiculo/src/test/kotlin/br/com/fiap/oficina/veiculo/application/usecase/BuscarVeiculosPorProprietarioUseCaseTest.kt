package br.com.fiap.oficina.veiculo.application.usecase

import br.com.fiap.oficina.veiculo.VeiculoFixtures
import br.com.fiap.oficina.veiculo.application.mapper.VeiculoMapper
import br.com.fiap.oficina.veiculo.domain.ProprietarioRef
import br.com.fiap.oficina.veiculo.domain.VeiculoRepository
import br.com.fiap.oficina.veiculo.domain.port.ConsultarProprietario
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

@DisplayName("Use case - Buscar Veículos por Proprietário")
class BuscarVeiculosPorProprietarioUseCaseTest {
    private val repositoryMock = mockk<VeiculoRepository>()
    private val consultarProprietarioMock = mockk<ConsultarProprietario>()
    private val mapper = VeiculoMapper()
    private val usecase = BuscarVeiculosPorProprietarioUseCaseImpl(repositoryMock, mapper, consultarProprietarioMock)

    @Test
    @DisplayName("Dado um proprietário com veículos, quando buscar, então deve retornar todos com o proprietário")
    fun givenProprietarioWithVeiculos_whenSearching_thenReturnAllWithProprietario() {
        val ref = ProprietarioRef.toUUID(VeiculoFixtures.PROPRIETARIO_ID)

        every { consultarProprietarioMock.buscarPorId(ref) } returns VeiculoFixtures.dadosProprietario()
        every { repositoryMock.buscarPorProprietario(ref) } returns
            listOf(
                VeiculoFixtures.veiculo(),
                VeiculoFixtures.veiculo(id = VeiculoFixtures.ID_2, placa = VeiculoFixtures.PLACA_OUTRA),
            )

        val resultado = usecase.executar(VeiculoFixtures.PROPRIETARIO_ID)

        assertEquals(2, resultado.size)
        assertEquals(VeiculoFixtures.ID_1, resultado.first().id)
        assertEquals(VeiculoFixtures.PLACA_OUTRA, resultado.last().placa)
        assertTrue(resultado.all { it.proprietario?.nome == VeiculoFixtures.PROPRIETARIO_NOME })

        verify(exactly = 1) { consultarProprietarioMock.buscarPorId(ref) }
        verify(exactly = 1) { repositoryMock.buscarPorProprietario(ref) }
    }

    @Test
    @DisplayName("Dado um proprietário sem veículos, quando buscar, então deve retornar lista vazia")
    fun givenProprietarioWithoutVeiculos_whenSearching_thenReturnEmptyList() {
        every { consultarProprietarioMock.buscarPorId(any()) } returns VeiculoFixtures.dadosProprietario()
        every { repositoryMock.buscarPorProprietario(any()) } returns emptyList()

        assertTrue(usecase.executar(VeiculoFixtures.PROPRIETARIO_ID).isEmpty())
    }

    @Test
    @DisplayName("Dado um proprietário inexistente, quando buscar, então deve retornar os veículos sem proprietário")
    fun givenNonExistentProprietario_whenSearching_thenReturnVeiculosWithoutProprietario() {
        every { consultarProprietarioMock.buscarPorId(any()) } returns null
        every { repositoryMock.buscarPorProprietario(any()) } returns listOf(VeiculoFixtures.veiculo())

        val resultado = usecase.executar(VeiculoFixtures.PROPRIETARIO_ID)

        assertEquals(1, resultado.size)
        assertNull(resultado.first().proprietario)
    }

    @Test
    @DisplayName("Dado um id inválido, quando buscar, então deve lançar exceção")
    fun givenInvalidId_whenSearching_thenThrowException() {
        assertFailsWith<IllegalArgumentException> { usecase.executar("nao-e-um-uuid") }

        verify(exactly = 0) { repositoryMock.buscarPorProprietario(any()) }
    }
}
