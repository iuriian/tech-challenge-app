package br.com.fiap.oficina.veiculo.infrastructure.acl

import br.com.fiap.oficina.cliente.application.port.ClienteQueryPort
import br.com.fiap.oficina.veiculo.VeiculoFixtures
import br.com.fiap.oficina.veiculo.domain.ProprietarioRef
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.UUID

@DisplayName("Infraestrutura - Adaptador de consulta de proprietário")
class ConsultarProprietarioAdapterTest {
    private val queryPortMock = mockk<ClienteQueryPort>()
    private val adapter = ConsultarProprietarioAdapter(queryPortMock, ProprietarioMapper())

    @Test
    @DisplayName("Dado um cliente cadastrado, quando buscar por id, então deve retornar os dados do proprietário")
    fun givenRegisteredCliente_whenSearchingById_thenReturnProprietarioData() {
        val ref = ProprietarioRef.toUUID(VeiculoFixtures.PROPRIETARIO_ID)

        every { queryPortMock.buscarPorId(ref.value) } returns VeiculoFixtures.clienteQueryDto()

        val resultado = adapter.buscarPorId(ref)

        assertEquals(ref, resultado?.ref)
        assertEquals(VeiculoFixtures.PROPRIETARIO_NOME, resultado?.nome)
        assertEquals(VeiculoFixtures.PROPRIETARIO_DOCUMENTO, resultado?.numeroDocumento)

        verify(exactly = 1) { queryPortMock.buscarPorId(ref.value) }
    }

    @Test
    @DisplayName("Dado um cliente não cadastrado, quando buscar por id, então deve retornar nulo")
    fun givenUnregisteredCliente_whenSearchingById_thenReturnNull() {
        every { queryPortMock.buscarPorId(any<UUID>()) } returns null

        assertNull(adapter.buscarPorId(ProprietarioRef.toUUID(VeiculoFixtures.PROPRIETARIO_ID)))
    }
}
