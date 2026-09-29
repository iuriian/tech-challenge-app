package br.com.fiap.oficina.peca.domain

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.math.BigDecimal
import java.util.UUID
import kotlin.test.assertFailsWith

@DisplayName("Domínio - Peça")
class PecaTest {
    @Test
    @DisplayName("Dado dados válidos, quando criar peça, então deve ter sucesso")
    fun givenValidData_whenCreatingPeca_thenShouldSucceed() {
        val peca = novaPeca()

        assertEquals(CODIGO, peca.codigo)
        assertEquals(NOME, peca.nome)
        assertEquals("Pastilha dianteira", peca.descricao)
        assertEquals(FABRICANTE, peca.fabricante)
        assertEquals(FORNECEDOR, peca.fornecedor)
        assertEquals(PRECO_COMPRA, peca.precoCompra)
        assertEquals(PRECO_VENDA, peca.precoVenda)
        assertEquals(10, peca.quantidade)
        assertNotNull(peca.id)
        assertNotNull(peca.id.value)
    }

    @Test
    @DisplayName("Dado uma peça recém-criada, quando consultar o status, então deve estar inativa")
    fun givenNewlyCreatedPeca_whenCheckingStatus_thenShouldBeInactive() {
        assertFalse(novaPeca().ativo)
    }

    @Test
    @DisplayName("Dado duas peças criadas, quando gerar IDs, então deve ter IDs diferentes")
    fun givenTwoPecasCreated_whenGeneratingIds_thenShouldHaveDifferentIds() {
        assertNotEquals(novaPeca(codigo = "COD-1").id, novaPeca(codigo = "COD-2").id)
    }

    @ParameterizedTest(name = "código \"{0}\"")
    @ValueSource(strings = ["", " ", "   "])
    @DisplayName("Dado um código em branco, quando criar peça, então deve lançar exceção")
    fun givenBlankCodigo_whenCreatingPeca_thenThrowException(codigo: String) {
        val exception = assertFailsWith<IllegalArgumentException> { novaPeca(codigo = codigo) }

        assertEquals("Código da peça é obrigatório", exception.message)
    }

    @ParameterizedTest(name = "nome \"{0}\"")
    @ValueSource(strings = ["", " ", "   "])
    @DisplayName("Dado um nome em branco, quando criar peça, então deve lançar exceção")
    fun givenBlankNome_whenCreatingPeca_thenThrowException(nome: String) {
        val exception = assertFailsWith<IllegalArgumentException> { novaPeca(nome = nome) }

        assertEquals("Nome da peça é obrigatório", exception.message)
    }

    @Test
    @DisplayName("Dado um preço de compra negativo, quando criar peça, então deve lançar exceção")
    fun givenNegativePrecoCompra_whenCreatingPeca_thenThrowException() {
        val exception =
            assertFailsWith<IllegalArgumentException> {
                novaPeca(precoCompra = BigDecimal("-0.01"))
            }

        assertEquals("Preço de compra não pode ser menor que zero", exception.message)
    }

    @Test
    @DisplayName("Dado um preço de venda negativo, quando criar peça, então deve lançar exceção")
    fun givenNegativePrecoVenda_whenCreatingPeca_thenThrowException() {
        val exception =
            assertFailsWith<IllegalArgumentException> {
                novaPeca(precoVenda = BigDecimal("-0.01"))
            }

        assertEquals("Preço de venda não pode ser menor que zero", exception.message)
    }

    @Test
    @DisplayName("Dado uma quantidade negativa, quando criar peça, então deve lançar exceção")
    fun givenNegativeQuantidade_whenCreatingPeca_thenThrowException() {
        val exception = assertFailsWith<IllegalArgumentException> { novaPeca(quantidade = -1) }

        assertEquals("Quantidade em estoque não pode ser menor que zero", exception.message)
    }

    @Test
    @DisplayName("Dado preços e quantidade zerados, quando criar peça, então deve ter sucesso")
    fun givenZeroedPricesAndQuantity_whenCreatingPeca_thenShouldSucceed() {
        val peca =
            novaPeca(
                precoCompra = BigDecimal.ZERO,
                precoVenda = BigDecimal.ZERO,
                quantidade = 0,
            )

        assertEquals(BigDecimal.ZERO, peca.precoCompra)
        assertEquals(BigDecimal.ZERO, peca.precoVenda)
        assertEquals(0, peca.quantidade)
    }

    @Test
    @DisplayName("Dado descrição e fornecedor nulos, quando criar peça, então devem virar texto vazio")
    fun givenNullDescricaoAndFornecedor_whenCreatingPeca_thenBecomeEmptyText() {
        val peca = novaPeca(descricao = null, fornecedor = null)

        assertEquals("", peca.descricao)
        assertEquals("", peca.fornecedor)
    }

    @Test
    @DisplayName("Dado descrição e fornecedor nulos, quando atualizar peça, então devem virar texto vazio")
    fun givenNullDescricaoAndFornecedor_whenUpdatingPeca_thenBecomeEmptyText() {
        val atualizada =
            pecaReconstruida().atualizar(
                nome = NOME,
                codigo = CODIGO,
                descricao = null,
                fabricante = FABRICANTE,
                fornecedor = null,
                precoCompra = PRECO_COMPRA,
                precoVenda = PRECO_VENDA,
            )

        assertEquals("", atualizada.descricao)
        assertEquals("", atualizada.fornecedor)
    }

    @Test
    @DisplayName("Dado um ID válido, quando reconstruir peça, então deve preservar todos os campos")
    fun givenValidId_whenReconstructingPeca_thenPreserveAllFields() {
        val peca = pecaReconstruida(ativo = true)

        assertEquals(UUID.fromString(ID_VALIDO), peca.id.value)
        assertEquals(CODIGO, peca.codigo)
        assertEquals(NOME, peca.nome)
        assertEquals("Pastilha dianteira", peca.descricao)
        assertEquals(FABRICANTE, peca.fabricante)
        assertEquals(FORNECEDOR, peca.fornecedor)
        assertEquals(PRECO_COMPRA, peca.precoCompra)
        assertEquals(PRECO_VENDA, peca.precoVenda)
        assertEquals(10, peca.quantidade)
        assertTrue(peca.ativo)
    }

    @Test
    @DisplayName("Dado um ID inválido, quando reconstruir peça, então deve lançar exceção")
    fun givenInvalidId_whenReconstructingPeca_thenThrowException() {
        assertFailsWith<IllegalArgumentException> { pecaReconstruida(id = "nao-e-um-uuid") }
    }

    @Test
    @DisplayName("Dado dados inválidos, quando reconstruir peça, então as invariantes também devem valer")
    fun givenInvalidData_whenReconstructingPeca_thenInvariantsAlsoApply() {
        val exception =
            assertFailsWith<IllegalArgumentException> {
                pecaReconstruida(quantidade = -1)
            }

        assertEquals("Quantidade em estoque não pode ser menor que zero", exception.message)
    }

    @Test
    @DisplayName("Dado uma peça, quando atualizar, então deve alterar os campos editáveis")
    fun givenPeca_whenUpdating_thenChangeEditableFields() {
        val peca = pecaReconstruida(ativo = true)

        val atualizada =
            peca.atualizar(
                nome = "Pastilha de freio cerâmica",
                codigo = "PC-999",
                descricao = "Pastilha cerâmica dianteira",
                fabricante = "Brembo",
                fornecedor = "AutoPeças SP",
                precoCompra = BigDecimal("55.00"),
                precoVenda = BigDecimal("99.90"),
            )

        assertEquals("Pastilha de freio cerâmica", atualizada.nome)
        assertEquals("PC-999", atualizada.codigo)
        assertEquals("Pastilha cerâmica dianteira", atualizada.descricao)
        assertEquals("Brembo", atualizada.fabricante)
        assertEquals("AutoPeças SP", atualizada.fornecedor)
        assertEquals(BigDecimal("55.00"), atualizada.precoCompra)
        assertEquals(BigDecimal("99.90"), atualizada.precoVenda)
    }

    @Test
    @DisplayName("Dado uma peça, quando atualizar, então deve preservar id, quantidade e status")
    fun givenPeca_whenUpdating_thenPreserveIdQuantidadeAndStatus() {
        val peca = pecaReconstruida(ativo = true)

        val atualizada =
            peca.atualizar(
                nome = "Outro nome",
                codigo = "PC-999",
                fabricante = "Outro fabricante",
                precoCompra = BigDecimal("1.00"),
                precoVenda = BigDecimal("2.00"),
            )

        assertEquals(peca.id, atualizada.id)
        assertEquals(peca.quantidade, atualizada.quantidade)
        assertEquals(peca.ativo, atualizada.ativo)
    }

    @Test
    @DisplayName("Dado uma peça inativa, quando ativar, então deve ficar ativa")
    fun givenInactivePeca_whenActivating_thenShouldBecomeActive() {
        assertTrue(pecaReconstruida(ativo = false).ativar().ativo)
    }

    @Test
    @DisplayName("Dado uma peça ativa, quando desativar, então deve ficar inativa")
    fun givenActivePeca_whenDeactivating_thenShouldBecomeInactive() {
        assertFalse(pecaReconstruida(ativo = true).desativar().ativo)
    }

    @Test
    @DisplayName("Dado uma peça ativa, quando ativar novamente, então deve permanecer ativa")
    fun givenActivePeca_whenActivatingAgain_thenShouldRemainActive() {
        assertTrue(pecaReconstruida(ativo = true).ativar().ativo)
    }

    @Test
    @DisplayName("Dado uma quantidade válida, quando atualizar estoque, então deve substituir a quantidade")
    fun givenValidQuantity_whenUpdatingStock_thenReplaceQuantity() {
        assertEquals(42, pecaReconstruida().atualizarEstoque(42).quantidade)
    }

    @Test
    @DisplayName("Dado zero, quando atualizar estoque, então deve zerar a quantidade")
    fun givenZero_whenUpdatingStock_thenSetQuantityToZero() {
        assertEquals(0, pecaReconstruida().atualizarEstoque(0).quantidade)
    }

    @Test
    @DisplayName("Dado uma quantidade negativa, quando atualizar estoque, então deve lançar exceção")
    fun givenNegativeQuantity_whenUpdatingStock_thenThrowException() {
        val exception =
            assertFailsWith<IllegalArgumentException> {
                pecaReconstruida().atualizarEstoque(-1)
            }

        assertEquals("Quantidade em estoque não pode ser negativa", exception.message)
    }

    @Test
    @DisplayName("Dado estoque suficiente, quando retirar peças, então deve subtrair a quantidade")
    fun givenSufficientStock_whenWithdrawingPecas_thenSubtractQuantity() {
        assertEquals(7, pecaReconstruida(quantidade = 10).retirarPecas(3).quantidade)
    }

    @Test
    @DisplayName("Dado uma retirada igual ao estoque, quando retirar peças, então deve zerar o estoque")
    fun givenWithdrawalEqualToStock_whenWithdrawingPecas_thenStockBecomesZero() {
        assertEquals(0, pecaReconstruida(quantidade = 10).retirarPecas(10).quantidade)
    }

    @ParameterizedTest(name = "quantidade {0}")
    @ValueSource(ints = [0, -1, -10])
    @DisplayName("Dado uma quantidade não positiva, quando retirar peças, então deve lançar exceção")
    fun givenNonPositiveQuantity_whenWithdrawingPecas_thenThrowException(qtd: Int) {
        val exception =
            assertFailsWith<IllegalArgumentException> {
                pecaReconstruida(quantidade = 10).retirarPecas(qtd)
            }

        assertEquals("Quantidade para retirada deve ser maior que zero", exception.message)
    }

    @Test
    @DisplayName("Dado estoque insuficiente, quando retirar peças, então deve lançar exceção")
    fun givenInsufficientStock_whenWithdrawingPecas_thenThrowException() {
        val exception =
            assertFailsWith<IllegalArgumentException> {
                pecaReconstruida(quantidade = 5).retirarPecas(6)
            }

        assertEquals("Quantidade em estoque insuficiente", exception.message)
    }

    @Test
    @DisplayName("Dado uma quantidade válida, quando repor peças, então deve somar ao estoque")
    fun givenValidQuantity_whenReplenishingPecas_thenAddToStock() {
        assertEquals(15, pecaReconstruida(quantidade = 10).reporPecas(5).quantidade)
    }

    @ParameterizedTest(name = "quantidade {0}")
    @ValueSource(ints = [0, -1, -10])
    @DisplayName("Dado uma quantidade não positiva, quando repor peças, então deve lançar exceção")
    fun givenNonPositiveQuantity_whenReplenishingPecas_thenThrowException(qtd: Int) {
        val exception =
            assertFailsWith<IllegalArgumentException> {
                pecaReconstruida(quantidade = 10).reporPecas(qtd)
            }

        assertEquals("Quantidade para reposição deve ser maior que zero", exception.message)
    }

    @Test
    @DisplayName("Dado uma operação de estoque, quando executada, então não deve mutar a peça original")
    fun givenStockOperation_whenExecuted_thenOriginalPecaIsNotMutated() {
        val original = pecaReconstruida(quantidade = 10)

        original.retirarPecas(4)
        original.reporPecas(4)
        original.atualizarEstoque(99)
        original.ativar()

        assertEquals(10, original.quantidade)
        assertFalse(original.ativo)
    }

    private fun novaPeca(
        codigo: String = CODIGO,
        nome: String = NOME,
        descricao: String? = "Pastilha dianteira",
        fornecedor: String? = FORNECEDOR,
        precoCompra: BigDecimal = PRECO_COMPRA,
        precoVenda: BigDecimal = PRECO_VENDA,
        quantidade: Int = 10,
    ) = Peca.criar(
        codigo = codigo,
        nome = nome,
        descricao = descricao,
        fabricante = FABRICANTE,
        fornecedor = fornecedor,
        precoCompra = precoCompra,
        precoVenda = precoVenda,
        quantidade = quantidade,
    )

    private fun pecaReconstruida(
        id: String = ID_VALIDO,
        quantidade: Int = 10,
        ativo: Boolean = false,
    ) = Peca.reconstruir(
        id = id,
        codigo = CODIGO,
        nome = NOME,
        descricao = "Pastilha dianteira",
        fabricante = FABRICANTE,
        fornecedor = FORNECEDOR,
        precoCompra = PRECO_COMPRA,
        precoVenda = PRECO_VENDA,
        quantidade = quantidade,
        ativo = ativo,
    )

    private companion object {
        const val ID_VALIDO = "00000000-0000-0000-0000-000000000100"
        const val CODIGO = "PC-001"
        const val NOME = "Pastilha de freio"
        const val FABRICANTE = "Bosch"
        const val FORNECEDOR = "AutoPeças RJ"
        val PRECO_COMPRA: BigDecimal = BigDecimal("50.00")
        val PRECO_VENDA: BigDecimal = BigDecimal("89.90")
    }
}
