package br.com.fiap.oficina.peca.application.mapper

import br.com.fiap.oficina.peca.application.dto.PecaRequest
import br.com.fiap.oficina.peca.domain.Peca
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import kotlin.test.assertFailsWith

@DisplayName("Aplicação - Mapper de Peça")
class PecaMapperTest {
    private val mapper = PecaMapper()

    @Test
    @DisplayName("Dado um request válido, quando converter para domínio, então deve mapear todos os campos")
    fun givenValidRequest_whenConvertingToDomain_thenMapAllFields() {
        val peca = mapper.toDomain(request())

        assertEquals(CODIGO, peca.codigo)
        assertEquals(NOME, peca.nome)
        assertEquals(DESCRICAO, peca.descricao)
        assertEquals(FABRICANTE, peca.fabricante)
        assertEquals(FORNECEDOR, peca.fornecedor)
        assertEquals(PRECO_COMPRA, peca.precoCompra)
        assertEquals(PRECO_VENDA, peca.precoVenda)
        assertEquals(10, peca.quantidade)
        assertNotNull(peca.id.value)
    }

    @Test
    @DisplayName("Dado um request, quando converter para domínio, então a peça deve nascer inativa")
    fun givenRequest_whenConvertingToDomain_thenPecaStartsInactive() {
        assertFalse(mapper.toDomain(request()).ativo)
    }

    @Test
    @DisplayName("Dado um request inválido, quando converter para domínio, então deve propagar a validação de domínio")
    fun givenInvalidRequest_whenConvertingToDomain_thenPropagateDomainValidation() {
        val exception =
            assertFailsWith<IllegalArgumentException> {
                mapper.toDomain(request(nome = "   "))
            }

        assertEquals("Nome da peça é obrigatório", exception.message)
    }

    @Test
    @DisplayName("Dado uma peça de domínio, quando converter para response, então deve mapear todos os campos")
    fun givenDomainPeca_whenConvertingToResponse_thenMapAllFields() {
        val peca = pecaReconstruida(ativo = true)

        val response = mapper.toResponse(peca)

        assertEquals(ID_VALIDO, response.id)
        assertEquals(CODIGO, response.codigo)
        assertEquals(NOME, response.nome)
        assertEquals(DESCRICAO, response.descricao)
        assertEquals(FABRICANTE, response.fabricante)
        assertEquals(FORNECEDOR, response.fornecedor)
        assertEquals(PRECO_COMPRA, response.precoDeCompra)
        assertEquals(PRECO_VENDA, response.precoDeVenda)
        assertEquals(10, response.qtdEstoque)
        assertTrue(response.ativo)
    }

    @Test
    @DisplayName("Dado uma peça inativa, quando converter para response, então deve refletir o status inativo")
    fun givenInactivePeca_whenConvertingToResponse_thenReflectInactiveStatus() {
        assertFalse(mapper.toResponse(pecaReconstruida(ativo = false)).ativo)
    }

    @Test
    @DisplayName("Dado um request, quando converter ida e volta, então deve preservar os dados de negócio")
    fun givenRequest_whenConvertingRoundTrip_thenPreserveBusinessData() {
        val request = request()

        val response = mapper.toResponse(mapper.toDomain(request))

        assertEquals(request.codigo, response.codigo)
        assertEquals(request.nome, response.nome)
        assertEquals(request.descricao, response.descricao)
        assertEquals(request.fabricante, response.fabricante)
        assertEquals(request.fornecedor, response.fornecedor)
        assertEquals(request.precoDeCompra, response.precoDeCompra)
        assertEquals(request.precoDeVenda, response.precoDeVenda)
        assertEquals(request.qtdEstoque, response.qtdEstoque)
    }

    private fun request(codigo: String = CODIGO, nome: String = NOME) = PecaRequest(
        codigo = codigo,
        nome = nome,
        descricao = DESCRICAO,
        fabricante = FABRICANTE,
        fornecedor = FORNECEDOR,
        precoDeCompra = PRECO_COMPRA,
        precoDeVenda = PRECO_VENDA,
        qtdEstoque = 10,
    )

    private fun pecaReconstruida(ativo: Boolean) = Peca.reconstruir(
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
