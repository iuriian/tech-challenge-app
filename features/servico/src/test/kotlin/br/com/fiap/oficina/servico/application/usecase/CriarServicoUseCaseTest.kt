package br.com.fiap.oficina.servico.application.usecase

import br.com.fiap.oficina.servico.ServicoFixtures
import br.com.fiap.oficina.servico.application.mapper.ServicoMapper
import br.com.fiap.oficina.servico.domain.Servico
import br.com.fiap.oficina.servico.domain.ServicoRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import kotlin.test.assertFailsWith

@DisplayName("Use case - Criar Serviço")
class CriarServicoUseCaseTest {
    private val repositoryMock = mockk<ServicoRepository>()
    private val usecase = CriarServicoUseCaseImpl(repositoryMock, ServicoMapper())

    @Test
    @DisplayName("Dado um request válido, quando criar serviço, então deve persistir e retornar o response")
    fun givenValidRequest_whenCreatingServico_thenPersistAndReturnResponse() {
        val capturado = slot<Servico>()

        every { repositoryMock.salvar(capture(capturado)) } answers { capturado.captured }

        val response = usecase.executar(ServicoFixtures.request())

        assertEquals(ServicoFixtures.DESCRICAO, response.descricao)
        assertEquals(ServicoFixtures.VALOR, response.valor)
        assertTrue(response.ativo)
        assertEquals(capturado.captured.id.toString(), response.id)

        verify(exactly = 1) { repositoryMock.salvar(any()) }
    }

    @Test
    @DisplayName("Dado um request com descrição em branco, quando criar serviço, então não deve persistir")
    fun givenRequestWithBlankDescription_whenCreatingServico_thenDoNotPersist() {
        assertFailsWith<IllegalArgumentException> {
            usecase.executar(ServicoFixtures.request(descricao = " "))
        }

        verify(exactly = 0) { repositoryMock.salvar(any()) }
    }

    @Test
    @DisplayName("Dado um request com valor negativo, quando criar serviço, então não deve persistir")
    fun givenRequestWithNegativeValue_whenCreatingServico_thenDoNotPersist() {
        assertFailsWith<IllegalArgumentException> {
            usecase.executar(ServicoFixtures.request(valor = BigDecimal("-10.00")))
        }

        verify(exactly = 0) { repositoryMock.salvar(any()) }
    }
}
