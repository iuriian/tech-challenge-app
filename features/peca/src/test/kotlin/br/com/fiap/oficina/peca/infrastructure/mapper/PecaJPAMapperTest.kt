package br.com.fiap.oficina.peca.infrastructure.mapper

import br.com.fiap.oficina.peca.domain.Peca
import br.com.fiap.oficina.peca.infrastructure.persistence.PecaJPA
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.math.BigDecimal
import java.util.UUID
import kotlin.test.assertFailsWith

@DisplayName("Infraestrutura - Mapper JPA de Peça")
class PecaJPAMapperTest {
    private val mapper = PecaJPAMapper()

    @Test
    @DisplayName("Dado uma peça de domínio, quando converter para JPA, então deve mapear todos os campos")
    fun givenDomainPeca_whenConvertingToJPA_thenMapAllFields() {
        val jpa = mapper.toJpa(peca(ativo = true))

        assertEquals(UUID.fromString(ID_VALIDO), jpa.id)
        assertEquals(CODIGO, jpa.codigo)
        assertEquals(NOME, jpa.nome)
        assertEquals(DESCRICAO, jpa.descricao)
        assertEquals(FABRICANTE, jpa.fabricante)
        assertEquals(FORNECEDOR, jpa.fornecedor)
        assertEquals(PRECO_COMPRA, jpa.precoCompra)
        assertEquals(PRECO_VENDA, jpa.precoVenda)
        assertEquals(10, jpa.quantidade)
        assertTrue(jpa.ativo)
    }

    @ParameterizedTest(name = "ativo {0}")
    @ValueSource(booleans = [true, false])
    @DisplayName("Dado uma entidade JPA, quando converter para domínio, então deve mapear todos os campos")
    fun givenJPAEntity_whenConvertingToDomain_thenMapAllFields(ativo: Boolean) {
        val peca = mapper.toDomain(pecaJPA(ativo = ativo))

        assertEquals(UUID.fromString(ID_VALIDO), peca.id.value)
        assertEquals(CODIGO, peca.codigo)
        assertEquals(NOME, peca.nome)
        assertEquals(DESCRICAO, peca.descricao)
        assertEquals(FABRICANTE, peca.fabricante)
        assertEquals(FORNECEDOR, peca.fornecedor)
        assertEquals(PRECO_COMPRA, peca.precoCompra)
        assertEquals(PRECO_VENDA, peca.precoVenda)
        assertEquals(10, peca.quantidade)
        assertEquals(ativo, peca.ativo)
    }

    @Test
    @DisplayName("Dado uma peça de domínio, quando converter ida e volta, então deve preservar a igualdade")
    fun givenDomainPeca_whenConvertingRoundTrip_thenPreserveEquality() {
        val peca = peca(ativo = true)

        assertEquals(peca, mapper.toDomain(mapper.toJpa(peca)))
    }

    @Test
    @DisplayName("Dado uma entidade JPA, quando converter ida e volta, então deve preservar os campos")
    fun givenJPAEntity_whenConvertingRoundTrip_thenPreserveFields() {
        val jpa = pecaJPA(ativo = false)

        val resultado = mapper.toJpa(mapper.toDomain(jpa))

        assertEquals(jpa.id, resultado.id)
        assertEquals(jpa.codigo, resultado.codigo)
        assertEquals(jpa.quantidade, resultado.quantidade)
        assertFalse(resultado.ativo)
    }

    @Test
    @DisplayName("Dado uma entidade JPA com quantidade negativa, quando converter, então deve lançar exceção")
    fun givenJPAEntityWithNegativeQuantity_whenConverting_thenThrowException() {
        val exception =
            assertFailsWith<IllegalArgumentException> {
                mapper.toDomain(pecaJPA(quantidade = -5))
            }

        assertEquals("Quantidade em estoque não pode ser menor que zero", exception.message)
    }

    @Test
    @DisplayName("Dado uma entidade JPA com id inválido, quando converter para domínio, então deve lançar exceção")
    fun givenJPAEntityWithInvalidId_whenConvertingToDomain_thenThrowException() {
        val peca = peca(ativo = false)

        assertFailsWith<IllegalArgumentException> {
            Peca.reconstruir(
                id = "id-invalido",
                codigo = peca.codigo,
                nome = peca.nome,
                descricao = peca.descricao,
                fabricante = peca.fabricante,
                fornecedor = peca.fornecedor,
                precoCompra = peca.precoCompra,
                precoVenda = peca.precoVenda,
                quantidade = peca.quantidade,
                ativo = peca.ativo,
            )
        }
    }

    private fun peca(ativo: Boolean) =
        Peca.reconstruir(
            id = ID_VALIDO,
            codigo = CODIGO,
            nome = NOME,
            descricao = DESCRICAO,
            fabricante = FABRICANTE,
            fornecedor = FORNECEDOR,
            precoCompra = PRECO_COMPRA,
            precoVenda = PRECO_VENDA,
            quantidade = 10,
            ativo = ativo,
        )

    private fun pecaJPA(
        quantidade: Int = 10,
        ativo: Boolean = false,
    ) = PecaJPA(
        id = UUID.fromString(ID_VALIDO),
        codigo = CODIGO,
        nome = NOME,
        descricao = DESCRICAO,
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
        const val DESCRICAO = "Pastilha dianteira"
        const val FABRICANTE = "Bosch"
        const val FORNECEDOR = "AutoPeças RJ"
        val PRECO_COMPRA: BigDecimal = BigDecimal("50.00")
        val PRECO_VENDA: BigDecimal = BigDecimal("89.90")
    }
}
