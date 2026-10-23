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

@DisplayName("Use case - Listar Veículos")
class ListarVeiculosUseCaseTest {
    private val repositoryMock = mockk<VeiculoRepository>()
    private val consultarProprietarioMock = mockk<ConsultarProprietario>()
    private val mapper = VeiculoMapper()
    private val usecase = ListarVeiculosUseCaseImpl(repositoryMock, mapper, consultarProprietarioMock)

    @Test
    @DisplayName("Dado veículos cadastrados, quando listar, então deve retornar todos com os seus proprietários")
    fun givenRegisteredVeiculos_whenListing_thenReturnAllWithTheirProprietarios() {
        val primeiroRef = ProprietarioRef.toUUID(VeiculoFixtures.PROPRIETARIO_ID)
        val segundoRef = ProprietarioRef.toUUID(VeiculoFixtures.PROPRIETARIO_ID_2)

        every { repositoryMock.listarTodos() } returns
            listOf(
                VeiculoFixtures.veiculo(),
                VeiculoFixtures.veiculo(
                    id = VeiculoFixtures.ID_2,
                    placa = VeiculoFixtures.PLACA_OUTRA,
                    proprietarioRef = VeiculoFixtures.PROPRIETARIO_ID_2,
                ),
            )
        every { consultarProprietarioMock.buscarPorId(primeiroRef) } returns VeiculoFixtures.dadosProprietario()
        every { consultarProprietarioMock.buscarPorId(segundoRef) } returns
            VeiculoFixtures.dadosProprietario(id = VeiculoFixtures.PROPRIETARIO_ID_2, nome = "Maria Souza")

        val resultado = usecase.executar()

        assertEquals(2, resultado.size)
        assertEquals(VeiculoFixtures.PROPRIETARIO_NOME, resultado.first().proprietario?.nome)
        assertEquals("Maria Souza", resultado.last().proprietario?.nome)

        verify(exactly = 1) { consultarProprietarioMock.buscarPorId(primeiroRef) }
        verify(exactly = 1) { consultarProprietarioMock.buscarPorId(segundoRef) }
    }

    @Test
    @DisplayName(
        "Dado veículos do mesmo proprietário, quando listar, então deve consultar o proprietário uma única vez",
    )
    fun givenVeiculosOfSameProprietario_whenListing_thenQueryProprietarioOnlyOnce() {
        every { repositoryMock.listarTodos() } returns
            listOf(
                VeiculoFixtures.veiculo(),
                VeiculoFixtures.veiculo(id = VeiculoFixtures.ID_2, placa = VeiculoFixtures.PLACA_OUTRA),
            )
        every { consultarProprietarioMock.buscarPorId(any()) } returns VeiculoFixtures.dadosProprietario()

        val resultado = usecase.executar()

        assertEquals(2, resultado.size)
        assertTrue(resultado.all { it.proprietario?.id == VeiculoFixtures.PROPRIETARIO_ID })

        verify(exactly = 1) { consultarProprietarioMock.buscarPorId(any()) }
    }

    @Test
    @DisplayName("Dado nenhum veículo cadastrado, quando listar, então deve retornar lista vazia")
    fun givenNoVeiculos_whenListing_thenReturnEmptyList() {
        every { repositoryMock.listarTodos() } returns emptyList()

        assertTrue(usecase.executar().isEmpty())

        verify(exactly = 0) { consultarProprietarioMock.buscarPorId(any()) }
    }

    @Test
    @DisplayName("Dado um proprietário inexistente, quando listar, então deve retornar o veículo sem proprietário")
    fun givenNonExistentProprietario_whenListing_thenReturnVeiculoWithoutProprietario() {
        every { repositoryMock.listarTodos() } returns listOf(VeiculoFixtures.veiculo())
        every { consultarProprietarioMock.buscarPorId(any()) } returns null

        val resultado = usecase.executar()

        assertEquals(1, resultado.size)
        assertNull(resultado.first().proprietario)
    }
}
