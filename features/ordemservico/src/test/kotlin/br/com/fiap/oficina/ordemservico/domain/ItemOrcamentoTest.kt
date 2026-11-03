package br.com.fiap.oficina.ordemservico.domain

import br.com.fiap.oficina.ordemservico.OrdemServicoFixtures
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.math.BigDecimal
import kotlin.test.assertFailsWith

@DisplayName("Domínio - Item do Orçamento")
class ItemOrcamentoTest {
    @Test
    @DisplayName("Dado os dados de uma peça, quando criar o item, então deve copiar o preço e o código")
    fun givenPecaData_whenCreatingItem_thenCopyPriceAndCode() {
        val item = ItemOrcamento.dePeca(peca = OrdemServicoFixtures.dadosPeca(), quantidade = BigDecimal("3"))

        assertEquals(TipoItemOrcamento.PECA, item.tipo)
        assertEquals(OrdemServicoFixtures.PECA_ID, item.referenciaId.toString())
        assertEquals(OrdemServicoFixtures.PECA_NOME, item.descricao)
        assertEquals(OrdemServicoFixtures.PECA_PRECO, item.valorUnitario)
        assertEquals(OrdemServicoFixtures.PECA_CODIGO, item.codigoReferencia)
        assertEquals(BigDecimal("269.70"), item.subtotal)
    }

    @Test
    @DisplayName("Dado os dados de um serviço, quando criar o item, então deve copiar a descrição e o valor")
    fun givenServicoData_whenCreatingItem_thenCopyDescriptionAndValue() {
        val item = ItemOrcamento.deServico(servico = OrdemServicoFixtures.dadosServico())

        assertEquals(TipoItemOrcamento.SERVICO, item.tipo)
        assertEquals(OrdemServicoFixtures.SERVICO_DESCRICAO, item.descricao)
        assertEquals(OrdemServicoFixtures.SERVICO_VALOR, item.valorUnitario)
        assertEquals(BigDecimal.ONE, item.quantidade)
        assertNull(item.codigoReferencia)
        assertEquals(OrdemServicoFixtures.SERVICO_VALOR, item.subtotal)
    }

    @Test
    @DisplayName("Dado uma quantidade informada, quando criar o item de serviço, então deve multiplicar o subtotal")
    fun givenInformedQuantity_whenCreatingServicoItem_thenMultiplySubtotal() {
        val item =
            ItemOrcamento.deServico(
                servico = OrdemServicoFixtures.dadosServico(),
                quantidade = BigDecimal("2"),
            )

        assertEquals(BigDecimal("300.00"), item.subtotal)
    }

    @ParameterizedTest(name = "descricao [{0}]")
    @ValueSource(strings = ["", " "])
    @DisplayName("Dado uma descrição em branco, quando criar o item, então deve lançar exceção")
    fun givenBlankDescription_whenCreatingItem_thenThrowException(descricao: String) {
        val exception =
            assertFailsWith<IllegalArgumentException> {
                item(descricao = descricao)
            }

        assertEquals("Descrição do item do orçamento é obrigatória", exception.message)
    }

    @Test
    @DisplayName("Dado um valor unitário negativo, quando criar o item, então deve lançar exceção")
    fun givenNegativeUnitValue_whenCreatingItem_thenThrowException() {
        val exception = assertFailsWith<IllegalArgumentException> { item(valorUnitario = BigDecimal("-0.01")) }

        assertEquals("Valor unitário do item não pode ser negativo", exception.message)
    }

    @Test
    @DisplayName("Dado um valor unitário zerado, quando criar o item, então deve ser aceito")
    fun givenZeroUnitValue_whenCreatingItem_thenAccept() {
        assertEquals(BigDecimal.ZERO, item(valorUnitario = BigDecimal.ZERO).valorUnitario)
    }

    @ParameterizedTest(name = "quantidade {0}")
    @ValueSource(strings = ["0", "-1"])
    @DisplayName("Dado uma quantidade não positiva, quando criar o item, então deve lançar exceção")
    fun givenNonPositiveQuantity_whenCreatingItem_thenThrowException(quantidade: String) {
        val exception = assertFailsWith<IllegalArgumentException> { item(quantidade = BigDecimal(quantidade)) }

        assertEquals("Quantidade do item deve ser maior que zero", exception.message)
    }

    @Test
    @DisplayName("Dado uma peça sem código de referência, quando criar o item, então deve lançar exceção")
    fun givenPecaWithoutReferenceCode_whenCreatingItem_thenThrowException() {
        val exception =
            assertFailsWith<IllegalArgumentException> {
                item(tipo = TipoItemOrcamento.PECA, codigoReferencia = null)
            }

        assertEquals("Código da peça é obrigatório", exception.message)
    }

    @Test
    @DisplayName("Dado uma peça com código em branco, quando criar o item, então deve lançar exceção")
    fun givenPecaWithBlankReferenceCode_whenCreatingItem_thenThrowException() {
        assertFailsWith<IllegalArgumentException> {
            item(tipo = TipoItemOrcamento.PECA, codigoReferencia = " ")
        }
    }

    @Test
    @DisplayName("Dado os mesmos dados, quando comparar os itens, então devem ser iguais")
    fun givenSameData_whenComparingItems_thenTheyAreEqual() {
        val primeiro = OrdemServicoFixtures.itemPeca()
        val segundo = OrdemServicoFixtures.itemPeca()

        assertEquals(primeiro, segundo)
        assertEquals(primeiro.hashCode(), segundo.hashCode())
        assertEquals(primeiro, primeiro.copy())
        assertEquals(TipoItemOrcamento.PECA, primeiro.component1())
        assertEquals(true, primeiro.toString().contains(OrdemServicoFixtures.PECA_NOME))
    }

    private fun item(
        tipo: TipoItemOrcamento = TipoItemOrcamento.SERVICO,
        descricao: String = OrdemServicoFixtures.SERVICO_DESCRICAO,
        valorUnitario: BigDecimal = OrdemServicoFixtures.SERVICO_VALOR,
        quantidade: BigDecimal = BigDecimal.ONE,
        codigoReferencia: String? = null,
    ) = ItemOrcamento(
        tipo = tipo,
        referenciaId = ItemReferenciaRef.toUUID(OrdemServicoFixtures.SERVICO_ID),
        descricao = descricao,
        valorUnitario = valorUnitario,
        quantidade = quantidade,
        codigoReferencia = codigoReferencia,
    )
}
