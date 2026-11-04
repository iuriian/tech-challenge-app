package br.com.fiap.oficina.servico.domain

import br.com.fiap.oficina.servico.ServicoFixtures
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.math.BigDecimal
import kotlin.test.assertFailsWith

@DisplayName("Domínio - Serviço")
class ServicoTest {
    @Test
    @DisplayName("Dado dados válidos, quando criar serviço, então deve gerar id e nascer ativo")
    fun givenValidData_whenCreatingServico_thenGenerateIdAndStartActive() {
        val servico = Servico.criar(descricao = ServicoFixtures.DESCRICAO, valor = ServicoFixtures.VALOR)

        assertEquals(ServicoFixtures.DESCRICAO, servico.descricao)
        assertEquals(ServicoFixtures.VALOR, servico.valor)
        assertTrue(servico.ativo)
    }

    @Test
    @DisplayName("Dado dois serviços criados, quando comparar os ids, então devem ser distintos")
    fun givenTwoCreatedServicos_whenComparingIds_thenTheyDiffer() {
        assertNotEquals(ServicoFixtures.novoServico().id, ServicoFixtures.novoServico().id)
    }

    @Test
    @DisplayName("Dado valor zero, quando criar serviço, então deve ser aceito")
    fun givenZeroValue_whenCreatingServico_thenAccept() {
        assertEquals(BigDecimal.ZERO, ServicoFixtures.novoServico(valor = BigDecimal.ZERO).valor)
    }

    @ParameterizedTest(name = "descricao [{0}]")
    @ValueSource(strings = ["", " ", "   "])
    @DisplayName("Dado uma descrição em branco, quando criar serviço, então deve lançar exceção")
    fun givenBlankDescription_whenCreatingServico_thenThrowException(descricao: String) {
        val exception =
            assertFailsWith<IllegalArgumentException> {
                Servico.criar(descricao = descricao, valor = ServicoFixtures.VALOR)
            }

        assertEquals("Descrição do serviço é obrigatória", exception.message)
    }

    @Test
    @DisplayName("Dado um valor negativo, quando criar serviço, então deve lançar exceção")
    fun givenNegativeValue_whenCreatingServico_thenThrowException() {
        val exception =
            assertFailsWith<IllegalArgumentException> {
                Servico.criar(descricao = ServicoFixtures.DESCRICAO, valor = BigDecimal("-0.01"))
            }

        assertEquals("Valor do serviço não pode ser negativo", exception.message)
    }

    @ParameterizedTest(name = "ativo {0}")
    @ValueSource(booleans = [true, false])
    @DisplayName("Dado dados persistidos, quando reconstruir serviço, então deve preservar o estado")
    fun givenPersistedData_whenReconstructingServico_thenPreserveState(ativo: Boolean) {
        val servico = ServicoFixtures.servico(ativo = ativo)

        assertEquals(ServicoFixtures.ID_1, servico.id.toString())
        assertEquals(ServicoFixtures.DESCRICAO, servico.descricao)
        assertEquals(ServicoFixtures.VALOR, servico.valor)
        assertEquals(ativo, servico.ativo)
    }

    @Test
    @DisplayName("Dado um id inválido, quando reconstruir serviço, então deve lançar exceção")
    fun givenInvalidId_whenReconstructingServico_thenThrowException() {
        assertFailsWith<IllegalArgumentException> { ServicoFixtures.servico(id = "nao-e-um-uuid") }
    }

    @Test
    @DisplayName("Dado um serviço, quando alterar a descrição, então deve manter os demais campos")
    fun givenServico_whenChangingDescription_thenKeepRemainingFields() {
        val servico = ServicoFixtures.servico()

        val alterado = servico.alterarDescricao(ServicoFixtures.DESCRICAO_ALTERNATIVA)

        assertEquals(ServicoFixtures.DESCRICAO_ALTERNATIVA, alterado.descricao)
        assertEquals(servico.id, alterado.id)
        assertEquals(servico.valor, alterado.valor)
        assertEquals(servico.ativo, alterado.ativo)
    }

    @Test
    @DisplayName("Dado um serviço, quando alterar a descrição para um valor em branco, então deve lançar exceção")
    fun givenServico_whenChangingDescriptionToBlank_thenThrowException() {
        val exception =
            assertFailsWith<IllegalArgumentException> {
                ServicoFixtures.servico().alterarDescricao(" ")
            }

        assertEquals("Descrição do serviço é obrigatória", exception.message)
    }

    @Test
    @DisplayName("Dado um serviço, quando alterar o valor, então deve manter os demais campos")
    fun givenServico_whenChangingValue_thenKeepRemainingFields() {
        val servico = ServicoFixtures.servico()

        val alterado = servico.alterarValor(ServicoFixtures.VALOR_ALTERNATIVO)

        assertEquals(ServicoFixtures.VALOR_ALTERNATIVO, alterado.valor)
        assertEquals(servico.id, alterado.id)
        assertEquals(servico.descricao, alterado.descricao)
    }

    @Test
    @DisplayName("Dado um serviço, quando alterar o valor para negativo, então deve lançar exceção")
    fun givenServico_whenChangingValueToNegative_thenThrowException() {
        val exception =
            assertFailsWith<IllegalArgumentException> {
                ServicoFixtures.servico().alterarValor(BigDecimal("-1.00"))
            }

        assertEquals("Valor do serviço não pode ser negativo", exception.message)
    }

    @Test
    @DisplayName("Dado um serviço ativo, quando desativar, então deve ficar inativo")
    fun givenActiveServico_whenDeactivating_thenBecomeInactive() {
        assertFalse(ServicoFixtures.servico(ativo = true).desativar().ativo)
    }

    @Test
    @DisplayName("Dado um serviço inativo, quando reativar, então deve ficar ativo")
    fun givenInactiveServico_whenReactivating_thenBecomeActive() {
        assertTrue(ServicoFixtures.servico(ativo = false).reativar().ativo)
    }

    @Test
    @DisplayName("Dado um serviço já inativo, quando desativar, então deve permanecer inativo")
    fun givenAlreadyInactiveServico_whenDeactivating_thenRemainInactive() {
        assertFalse(ServicoFixtures.servico(ativo = false).desativar().ativo)
    }
}
