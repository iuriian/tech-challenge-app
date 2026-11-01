package br.com.fiap.oficina.veiculo.application.usecase

import br.com.fiap.oficina.shared.domain.exception.EntityNotFoundException
import br.com.fiap.oficina.shared.domain.valueobject.Placa
import br.com.fiap.oficina.veiculo.VeiculoFixtures
import br.com.fiap.oficina.veiculo.application.mapper.VeiculoMapper
import br.com.fiap.oficina.veiculo.domain.VeiculoRepository
import br.com.fiap.oficina.veiculo.domain.port.ConsultarProprietario
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

@DisplayName("Use case - Buscar Veículo por Placa")
class BuscarVeiculoPorPlacaUseCaseTest {
    private val repositoryMock = mockk<VeiculoRepository>()
    private val consultarProprietarioMock = mockk<ConsultarProprietario>()
    private val mapper = VeiculoMapper()
    private val usecase = BuscarVeiculoPorPlacaUseCaseImpl(repositoryMock, mapper, consultarProprietarioMock)

    @Test
    @DisplayName("Dado uma placa cadastrada, quando buscar veículo, então deve retornar o veículo com o proprietário")
    fun givenRegisteredPlaca_whenSearchingVeiculo_thenReturnVeiculoWithProprietario() {
        val placa = Placa.criar(VeiculoFixtures.PLACA)

        every { repositoryMock.buscarPorPlaca(placa) } returns VeiculoFixtures.veiculo()
        every { consultarProprietarioMock.buscarPorId(any()) } returns VeiculoFixtures.dadosProprietario()

        val resultado = usecase.executar(VeiculoFixtures.PLACA)

        assertEquals(VeiculoFixtures.ID_1, resultado.id)
        assertEquals(VeiculoFixtures.PLACA, resultado.placa)
        assertEquals(VeiculoFixtures.PROPRIETARIO_NOME, resultado.proprietario?.nome)

        verify(exactly = 1) { repositoryMock.buscarPorPlaca(placa) }
    }

    @Test
    @DisplayName("Dado uma placa em minúsculas, quando buscar veículo, então deve normalizar antes de consultar")
    fun givenLowercasePlaca_whenSearchingVeiculo_thenNormalizeBeforeQuerying() {
        val placa = Placa.criar(VeiculoFixtures.PLACA)

        every { repositoryMock.buscarPorPlaca(placa) } returns VeiculoFixtures.veiculo()
        every { consultarProprietarioMock.buscarPorId(any()) } returns VeiculoFixtures.dadosProprietario()

        assertEquals(VeiculoFixtures.PLACA, usecase.executar("abc1234").placa)

        verify(exactly = 1) { repositoryMock.buscarPorPlaca(placa) }
    }

    @Test
    @DisplayName("Dado um proprietário inexistente, quando buscar veículo, então deve retornar sem o proprietário")
    fun givenNonExistentProprietario_whenSearchingVeiculo_thenReturnWithoutProprietario() {
        every { repositoryMock.buscarPorPlaca(any()) } returns VeiculoFixtures.veiculo()
        every { consultarProprietarioMock.buscarPorId(any()) } returns null

        assertNull(usecase.executar(VeiculoFixtures.PLACA).proprietario)
    }

    @Test
    @DisplayName("Dado uma placa não cadastrada, quando buscar veículo, então deve lançar exceção")
    fun givenUnregisteredPlaca_whenSearchingVeiculo_thenThrowException() {
        every { repositoryMock.buscarPorPlaca(any()) } returns null

        val exception =
            assertFailsWith<EntityNotFoundException> {
                usecase.executar(VeiculoFixtures.PLACA)
            }

        assertEquals("Veículo com placa ${VeiculoFixtures.PLACA} não encontrado", exception.message)

        verify(exactly = 0) { consultarProprietarioMock.buscarPorId(any()) }
    }

    @Test
    @DisplayName("Dado uma placa inválida, quando buscar veículo, então deve lançar exceção")
    fun givenInvalidPlaca_whenSearchingVeiculo_thenThrowException() {
        assertFailsWith<IllegalArgumentException> { usecase.executar("placa-invalida") }

        verify(exactly = 0) { repositoryMock.buscarPorPlaca(any()) }
    }
}
