package br.com.fiap.oficina.veiculo.application.usecase

import br.com.fiap.oficina.shared.domain.exception.BusinessRuleException
import br.com.fiap.oficina.shared.domain.exception.EntityNotFoundException
import br.com.fiap.oficina.shared.domain.valueobject.Placa
import br.com.fiap.oficina.veiculo.VeiculoFixtures
import br.com.fiap.oficina.veiculo.application.mapper.VeiculoMapper
import br.com.fiap.oficina.veiculo.domain.Veiculo
import br.com.fiap.oficina.veiculo.domain.VeiculoId
import br.com.fiap.oficina.veiculo.domain.VeiculoRepository
import br.com.fiap.oficina.veiculo.domain.port.ConsultarProprietario
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

@DisplayName("Use case - Atualizar Veículo")
class AtualizarVeiculoUseCaseTest {
    private val repositoryMock = mockk<VeiculoRepository>(relaxed = true)
    private val consultarProprietarioMock = mockk<ConsultarProprietario>()
    private val mapper = VeiculoMapper()
    private val usecase = AtualizarVeiculoUseCaseImpl(repositoryMock, mapper, consultarProprietarioMock)

    @Test
    @DisplayName("Dado dados válidos, quando atualizar veículo, então deve persistir e retornar o veículo atualizado")
    fun givenValidData_whenUpdatingVeiculo_thenPersistAndReturnUpdatedVeiculo() {
        val salvo = slot<Veiculo>()

        every { repositoryMock.buscarPorId(VeiculoId.toUUID(VeiculoFixtures.ID_1)) } returns VeiculoFixtures.veiculo()
        every { consultarProprietarioMock.buscarPorId(any()) } returns VeiculoFixtures.dadosProprietario()
        every { repositoryMock.salvar(capture(salvo)) } answers { salvo.captured }

        val request = VeiculoFixtures.veiculoRequest(marca = "Toyota", nome = "Corolla", ano = "2023")

        val resultado = usecase.executar(VeiculoFixtures.ID_1, request)

        assertEquals(VeiculoFixtures.ID_1, resultado.id)
        assertEquals("Toyota", resultado.marca)
        assertEquals("Corolla", resultado.nome)
        assertEquals("2023", resultado.ano)
        assertEquals(VeiculoFixtures.PLACA, resultado.placa)
        assertEquals(VeiculoFixtures.PROPRIETARIO_ID, resultado.proprietario?.id)
        assertEquals(VeiculoId.toUUID(VeiculoFixtures.ID_1), salvo.captured.id)

        verify(exactly = 1) { repositoryMock.salvar(any()) }
        verify(exactly = 0) { repositoryMock.buscarPorPlaca(any()) }
    }

    @Test
    @DisplayName("Dado uma nova placa livre, quando atualizar veículo, então deve persistir a nova placa")
    fun givenFreeNewPlaca_whenUpdatingVeiculo_thenPersistNewPlaca() {
        val salvo = slot<Veiculo>()

        every { repositoryMock.buscarPorId(any()) } returns VeiculoFixtures.veiculo()
        every { consultarProprietarioMock.buscarPorId(any()) } returns VeiculoFixtures.dadosProprietario()
        every { repositoryMock.buscarPorPlaca(Placa.criar(VeiculoFixtures.PLACA_OUTRA)) } returns null
        every { repositoryMock.salvar(capture(salvo)) } answers { salvo.captured }

        val request = VeiculoFixtures.veiculoRequest(placa = VeiculoFixtures.PLACA_OUTRA)

        val resultado = usecase.executar(VeiculoFixtures.ID_1, request)

        assertEquals(VeiculoFixtures.PLACA_OUTRA, resultado.placa)
        assertEquals(VeiculoFixtures.PLACA_OUTRA, salvo.captured.placa.value)

        verify(exactly = 1) { repositoryMock.buscarPorPlaca(Placa.criar(VeiculoFixtures.PLACA_OUTRA)) }
        verify(exactly = 1) { repositoryMock.salvar(any()) }
    }

    @Test
    @DisplayName("Dado uma nova placa já cadastrada, quando atualizar veículo, então deve lançar exceção")
    fun givenAlreadyRegisteredNewPlaca_whenUpdatingVeiculo_thenThrowException() {
        every { repositoryMock.buscarPorId(any()) } returns VeiculoFixtures.veiculo()
        every { consultarProprietarioMock.buscarPorId(any()) } returns VeiculoFixtures.dadosProprietario()
        every { repositoryMock.buscarPorPlaca(any()) } returns
            VeiculoFixtures.veiculo(id = VeiculoFixtures.ID_2, placa = VeiculoFixtures.PLACA_OUTRA)

        val request = VeiculoFixtures.veiculoRequest(placa = VeiculoFixtures.PLACA_OUTRA)

        val exception =
            assertFailsWith<BusinessRuleException> {
                usecase.executar(VeiculoFixtures.ID_1, request)
            }

        assertEquals(
            "Já existe um veículo cadastrado com a placa: ${VeiculoFixtures.PLACA_OUTRA}",
            exception.message,
        )

        verify(exactly = 0) { repositoryMock.salvar(any()) }
    }

    @Test
    @DisplayName("Dado um id inexistente, quando atualizar veículo, então deve lançar exceção")
    fun givenNonExistentId_whenUpdatingVeiculo_thenThrowException() {
        every { repositoryMock.buscarPorId(any()) } returns null

        val exception =
            assertFailsWith<EntityNotFoundException> {
                usecase.executar(VeiculoFixtures.ID_1, VeiculoFixtures.veiculoRequest())
            }

        assertEquals("Veículo não encontrado com o id: ${VeiculoFixtures.ID_1}", exception.message)

        verify(exactly = 0) { consultarProprietarioMock.buscarPorId(any()) }
        verify(exactly = 0) { repositoryMock.salvar(any()) }
    }

    @Test
    @DisplayName("Dado um proprietário inexistente, quando atualizar veículo, então deve lançar exceção")
    fun givenNonExistentProprietario_whenUpdatingVeiculo_thenThrowException() {
        every { repositoryMock.buscarPorId(any()) } returns VeiculoFixtures.veiculo()
        every { consultarProprietarioMock.buscarPorId(any()) } returns null

        val exception =
            assertFailsWith<BusinessRuleException> {
                usecase.executar(VeiculoFixtures.ID_1, VeiculoFixtures.veiculoRequest())
            }

        assertEquals(
            "Proprietário não encontrado com o id: ${VeiculoFixtures.PROPRIETARIO_ID}",
            exception.message,
        )

        verify(exactly = 0) { repositoryMock.salvar(any()) }
    }

    @Test
    @DisplayName("Dado um id inválido, quando atualizar veículo, então deve lançar exceção")
    fun givenInvalidId_whenUpdatingVeiculo_thenThrowException() {
        assertFailsWith<IllegalArgumentException> {
            usecase.executar("nao-e-um-uuid", VeiculoFixtures.veiculoRequest())
        }

        verify(exactly = 0) { repositoryMock.salvar(any()) }
    }
}
