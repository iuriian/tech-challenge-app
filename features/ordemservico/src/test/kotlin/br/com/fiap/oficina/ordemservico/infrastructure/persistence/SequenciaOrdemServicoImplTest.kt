package br.com.fiap.oficina.ordemservico.infrastructure.persistence

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import jakarta.persistence.EntityManager
import jakarta.persistence.Query
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.math.BigInteger

@DisplayName("Infraestrutura - Sequência da Ordem de Serviço")
class SequenciaOrdemServicoImplTest {
    private val entityManagerMock = mockk<EntityManager>()
    private val queryMock = mockk<Query>()
    private val sequencia = SequenciaOrdemServicoImpl(entityManagerMock)

    @Test
    @DisplayName("Dado a sequência do banco, quando obter o próximo valor, então deve retornar o número convertido")
    fun givenDatabaseSequence_whenGettingNextValue_thenReturnConvertedNumber() {
        every { entityManagerMock.createNativeQuery(any<String>()) } returns queryMock
        every { queryMock.singleResult } returns BigInteger.valueOf(42)

        assertEquals(42L, sequencia.proximoValor())

        verify(exactly = 1) {
            entityManagerMock.createNativeQuery("SELECT nextval('ordem_servico_numero_seq')")
        }
    }

    @Test
    @DisplayName("Dado um retorno inteiro, quando obter o próximo valor, então deve converter para long")
    fun givenIntegerResult_whenGettingNextValue_thenConvertToLong() {
        every { entityManagerMock.createNativeQuery(any<String>()) } returns queryMock
        every { queryMock.singleResult } returns 7

        assertEquals(7L, sequencia.proximoValor())
    }
}
