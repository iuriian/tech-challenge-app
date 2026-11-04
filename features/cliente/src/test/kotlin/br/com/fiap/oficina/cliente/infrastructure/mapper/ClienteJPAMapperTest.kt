package br.com.fiap.oficina.cliente.infrastructure.mapper

import br.com.fiap.oficina.cliente.ClienteFixtures
import br.com.fiap.oficina.cliente.domain.TipoPessoa
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.UUID

@DisplayName("Infraestrutura - Mapper JPA de Cliente")
class ClienteJPAMapperTest {
    private val mapper = ClienteJPAMapper()

    @Test
    @DisplayName("Dada uma entidade JPA, quando converter para domínio, então deve reconstruir o cliente")
    fun givenJpaEntity_whenConvertingToDomain_thenReconstructCliente() {
        val cliente = mapper.toDomain(ClienteFixtures.clienteJPA())

        assertEquals(ClienteFixtures.ID_1, cliente.id.toString())
        assertEquals(ClienteFixtures.NOME, cliente.nome)
        assertEquals(ClienteFixtures.CPF, cliente.documento.valor)
        assertEquals(ClienteFixtures.EMAIL, cliente.email)
        assertEquals(ClienteFixtures.ENDERECO_ID, cliente.endereco?.id?.toString())
        assertEquals(1, cliente.contatos.size)
        assertEquals(ClienteFixtures.CONTATO_ID, cliente.contatos.first().id.toString())
    }

    @Test
    @DisplayName("Dada uma entidade JPA de pessoa jurídica, quando converter para domínio, então deve usar o CNPJ")
    fun givenPessoaJuridicaJpaEntity_whenConvertingToDomain_thenUseCnpj() {
        val jpa =
            ClienteFixtures.clienteJPA(
                documento = ClienteFixtures.CNPJ,
                tipoPessoa = TipoPessoa.PESSOA_JURIDICA,
            )

        val cliente = mapper.toDomain(jpa)

        assertEquals(ClienteFixtures.CNPJ, cliente.documento.valor)
        assertEquals("11.222.333/0001-81", cliente.documento.formatado())
    }

    @Test
    @DisplayName("Dada uma entidade JPA sem endereço e contatos, quando converter para domínio, então deve ficar vazio")
    fun givenJpaEntityWithoutEnderecoAndContatos_whenConvertingToDomain_thenBecomeEmpty() {
        val jpa = ClienteFixtures.clienteJPA(endereco = null, contatos = mutableListOf())

        val cliente = mapper.toDomain(jpa)

        assertNull(cliente.endereco)
        assertTrue(cliente.contatos.isEmpty())
    }

    @Test
    @DisplayName("Dado um cliente de domínio, quando converter para JPA, então deve preencher a entidade")
    fun givenDomainCliente_whenConvertingToJpa_thenFillEntity() {
        val jpa = mapper.toJPA(ClienteFixtures.cliente())

        assertEquals(UUID.fromString(ClienteFixtures.ID_1), jpa.id)
        assertEquals(ClienteFixtures.NOME, jpa.nome)
        assertEquals(ClienteFixtures.CPF, jpa.documento.numero)
        assertEquals(TipoPessoa.PESSOA_FISICA, jpa.documento.tipoPessoa)
        assertEquals(ClienteFixtures.EMAIL, jpa.email)
        assertEquals(UUID.fromString(ClienteFixtures.ENDERECO_ID), jpa.endereco?.id)
        assertEquals(1, jpa.contatos.size)
    }

    @Test
    @DisplayName("Dado um cliente pessoa jurídica, quando converter para JPA, então deve gravar o tipo de pessoa")
    fun givenPessoaJuridicaCliente_whenConvertingToJpa_thenStoreTipoPessoa() {
        val jpa = mapper.toJPA(ClienteFixtures.cliente(documento = ClienteFixtures.CNPJ))

        assertEquals(ClienteFixtures.CNPJ, jpa.documento.numero)
        assertEquals(TipoPessoa.PESSOA_JURIDICA, jpa.documento.tipoPessoa)
    }

    @Test
    @DisplayName("Dado um cliente sem endereço e contatos, quando converter para JPA, então deve ficar vazio")
    fun givenClienteWithoutEnderecoAndContatos_whenConvertingToJpa_thenBecomeEmpty() {
        val jpa = mapper.toJPA(ClienteFixtures.cliente(endereco = null, contatos = emptyList()))

        assertNull(jpa.endereco)
        assertTrue(jpa.contatos.isEmpty())
    }

    @Test
    @DisplayName("Dada uma entidade JPA, quando converter ida e volta, então deve preservar os dados")
    fun givenJpaEntity_whenConvertingRoundTrip_thenPreserveData() {
        val jpa = ClienteFixtures.clienteJPA()

        val resultado = mapper.toJPA(mapper.toDomain(jpa))

        assertEquals(jpa, resultado)
    }
}
