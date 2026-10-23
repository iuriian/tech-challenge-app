package br.com.fiap.oficina.veiculo.application.usecase

import br.com.fiap.oficina.shared.domain.exception.EntityNotFoundException
import br.com.fiap.oficina.veiculo.VeiculoFixtures
import br.com.fiap.oficina.veiculo.application.mapper.VeiculoMapper
import br.com.fiap.oficina.veiculo.domain.ProprietarioRef
import br.com.fiap.oficina.veiculo.domain.VeiculoId
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

@DisplayName("Use case - Buscar Veículo por Id")
class BuscarVeiculoPorIdUseCaseTest {
    private val repositoryMock = mockk<VeiculoRepository>()
    private val consultarProprietarioMock = mockk<ConsultarProprietario>()
    private val mapper = VeiculoMapper()
    private val usecase = BuscarVeiculoPorIdUseCaseImpl(repositoryMock, mapper, consultarProprietarioMock)

    @Test
    @DisplayName("Dado um id existente, quando buscar veículo, então deve retornar o veículo com o proprietário")
    fun givenExistingId_whenSearchingVeiculo_thenReturnVeiculoWithProprietario() {
        val id = VeiculoId.toUUID(VeiculoFixtures.ID_1)
        val ref = ProprietarioRef.toUUID(VeiculoFixtures.PROPRIETARIO_ID)

        every { repositoryMock.buscarPorId(id) } returns VeiculoFixtures.veiculo()
        every { consultarProprietarioMock.buscarPorId(ref) } returns VeiculoFixtures.dadosProprietario()

        val resultado = usecase.executar(VeiculoFixtures.ID_1)

        assertEquals(VeiculoFixtures.ID_1, resultado.id)
        assertEquals(VeiculoFixtures.PLACA, resultado.placa)
        assertEquals(VeiculoFixtures.PROPRIETARIO_NOME, resultado.proprietario?.nome)

        verify(exactly = 1) { repositoryMock.buscarPorId(id) }
        verify(exactly = 1) { consultarProprietarioMock.buscarPorId(ref) }
    }

    @Test
    @DisplayName("Dado um proprietário inexistente, quando buscar veículo, então deve retornar sem o proprietário")
    fun givenNonExistentProprietario_whenSearchingVeiculo_thenReturnWithoutProprietario() {
        every { repositoryMock.buscarPorId(any()) } returns VeiculoFixtures.veiculo()
        every { consultarProprietarioMock.buscarPorId(any()) } returns null

        val resultado = usecase.executar(VeiculoFixtures.ID_1)

        assertEquals(VeiculoFixtures.ID_1, resultado.id)
        assertNull(resultado.proprietario)
    }

    @Test
    @DisplayName("Dado um id inexistente, quando buscar veículo, então deve lançar exceção")
    fun givenNonExistentId_whenSearchingVeiculo_thenThrowException() {
        every { repositoryMock.buscarPorId(any()) } returns null

        val exception =
            assertFailsWith<EntityNotFoundException> {
                usecase.executar(VeiculoFixtures.ID_1)
            }

        assertEquals("Veículo com ID ${VeiculoFixtures.ID_1} não encontrado", exception.message)

        verify(exactly = 0) { consultarProprietarioMock.buscarPorId(any()) }
    }

    @Test
    @DisplayName("Dado um id inválido, quando buscar veículo, então deve lançar exceção")
    fun givenInvalidId_whenSearchingVeiculo_thenThrowException() {
        assertFailsWith<IllegalArgumentException> { usecase.executar("nao-e-um-uuid") }

        verify(exactly = 0) { repositoryMock.buscarPorId(any()) }
    }
}
