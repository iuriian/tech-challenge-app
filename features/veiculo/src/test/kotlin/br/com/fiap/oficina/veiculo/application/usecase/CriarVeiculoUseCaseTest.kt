package br.com.fiap.oficina.veiculo.application.usecase

import br.com.fiap.oficina.shared.domain.exception.BusinessRuleException
import br.com.fiap.oficina.veiculo.VeiculoFixtures
import br.com.fiap.oficina.veiculo.application.mapper.VeiculoMapper
import br.com.fiap.oficina.veiculo.domain.Veiculo
import br.com.fiap.oficina.veiculo.domain.VeiculoRepository
import br.com.fiap.oficina.veiculo.domain.port.ConsultarProprietario
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

@DisplayName("Use case - Criar Veículo")
class CriarVeiculoUseCaseTest {
    private val repositoryMock = mockk<VeiculoRepository>(relaxed = true)
    private val consultarProprietarioMock = mockk<ConsultarProprietario>()
    private val mapper = VeiculoMapper()
    private val usecase = CriarVeiculoUseCaseImpl(repositoryMock, mapper, consultarProprietarioMock)

    @Test
    @DisplayName("Dado dados válidos, quando criar veículo, então deve persistir e retornar o veículo criado")
    fun givenValidData_whenCreatingVeiculo_thenPersistAndReturnCreatedVeiculo() {
        val salvo = slot<Veiculo>()

        every { repositoryMock.existeVeiculo(any()) } returns false
        every { consultarProprietarioMock.buscarPorId(any()) } returns VeiculoFixtures.dadosProprietario()
        every { repositoryMock.salvar(capture(salvo)) } answers { salvo.captured }

        val resultado = usecase.executar(VeiculoFixtures.veiculoRequest())

        assertNotNull(resultado)
        assertEquals(VeiculoFixtures.NOME, resultado.nome)
        assertEquals(VeiculoFixtures.MARCA, resultado.marca)
        assertEquals(VeiculoFixtures.PLACA, resultado.placa)
        assertEquals(VeiculoFixtures.PROPRIETARIO_ID, resultado.proprietario?.id)
        assertEquals(VeiculoFixtures.PROPRIETARIO_NOME, resultado.proprietario?.nome)
        assertEquals(VeiculoFixtures.PLACA, salvo.captured.placa.value)

        verify(exactly = 1) { repositoryMock.existeVeiculo(any()) }
        verify(exactly = 1) { consultarProprietarioMock.buscarPorId(any()) }
        verify(exactly = 1) { repositoryMock.salvar(any()) }
    }

    @Test
    @DisplayName("Dado uma placa já cadastrada, quando criar veículo, então deve lançar exceção")
    fun givenAlreadyRegisteredPlaca_whenCreatingVeiculo_thenThrowException() {
        every { repositoryMock.existeVeiculo(any()) } returns true

        val exception =
            assertFailsWith<BusinessRuleException> {
                usecase.executar(VeiculoFixtures.veiculoRequest())
            }

        assertEquals("Já existe veículo cadastrado com a placa ${VeiculoFixtures.PLACA}", exception.message)

        verify(exactly = 0) { consultarProprietarioMock.buscarPorId(any()) }
        verify(exactly = 0) { repositoryMock.salvar(any()) }
    }

    @Test
    @DisplayName("Dado um proprietário inexistente, quando criar veículo, então deve lançar exceção")
    fun givenNonExistentProprietario_whenCreatingVeiculo_thenThrowException() {
        every { repositoryMock.existeVeiculo(any()) } returns false
        every { consultarProprietarioMock.buscarPorId(any()) } returns null

        val exception =
            assertFailsWith<BusinessRuleException> {
                usecase.executar(VeiculoFixtures.veiculoRequest())
            }

        assertEquals(
            "Proprietário não encontrado com o id ${VeiculoFixtures.PROPRIETARIO_ID}",
            exception.message,
        )

        verify(exactly = 0) { repositoryMock.salvar(any()) }
    }

    @Test
    @DisplayName("Dado uma placa inválida, quando criar veículo, então deve lançar exceção")
    fun givenInvalidPlaca_whenCreatingVeiculo_thenThrowException() {
        assertFailsWith<IllegalArgumentException> {
            usecase.executar(VeiculoFixtures.veiculoRequest(placa = "1234ABC"))
        }

        verify(exactly = 0) { repositoryMock.existeVeiculo(any()) }
        verify(exactly = 0) { repositoryMock.salvar(any()) }
    }
}
