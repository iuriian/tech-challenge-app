package br.com.fiap.oficina.cliente.domain

import br.com.fiap.oficina.cliente.ClienteFixtures
import br.com.fiap.oficina.shared.domain.valueobject.Document
import br.com.fiap.oficina.shared.domain.valueobject.TipoDocumento
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

@DisplayName("Domínio - Tipo de Pessoa")
class TipoPessoaTest {
    @Test
    @DisplayName("Dado o tipo pessoa física, quando consultar o tipo de documento, então deve ser CPF")
    fun givenPessoaFisica_whenGettingTipoDocumento_thenReturnCpf() {
        assertEquals(TipoDocumento.CPF, TipoPessoa.PESSOA_FISICA.tipoDocumento)
    }

    @Test
    @DisplayName("Dado o tipo pessoa jurídica, quando consultar o tipo de documento, então deve ser CNPJ")
    fun givenPessoaJuridica_whenGettingTipoDocumento_thenReturnCnpj() {
        assertEquals(TipoDocumento.CNPJ, TipoPessoa.PESSOA_JURIDICA.tipoDocumento)
    }

    @Test
    @DisplayName("Dado um CPF, quando consultar o tipo de pessoa, então deve ser pessoa física")
    fun givenCpf_whenGettingTipoPessoa_thenReturnPessoaFisica() {
        assertEquals(TipoPessoa.PESSOA_FISICA, Document.from(ClienteFixtures.CPF).tipoPessoa)
    }

    @Test
    @DisplayName("Dado um CNPJ, quando consultar o tipo de pessoa, então deve ser pessoa jurídica")
    fun givenCnpj_whenGettingTipoPessoa_thenReturnPessoaJuridica() {
        assertEquals(TipoPessoa.PESSOA_JURIDICA, Document.from(ClienteFixtures.CNPJ).tipoPessoa)
    }

    @Test
    @DisplayName("Dado um nome válido, quando converter para tipo de pessoa, então deve retornar o enum")
    fun givenValidName_whenParsingTipoPessoa_thenReturnEnum() {
        assertEquals(TipoPessoa.PESSOA_FISICA, TipoPessoa.valueOf("PESSOA_FISICA"))
        assertEquals(2, TipoPessoa.entries.size)
    }

    @Test
    @DisplayName("Dado um nome inválido, quando converter para tipo de pessoa, então deve lançar exceção")
    fun givenInvalidName_whenParsingTipoPessoa_thenThrowException() {
        assertFailsWith<IllegalArgumentException> { TipoPessoa.valueOf("PESSOA_INEXISTENTE") }
    }
}
