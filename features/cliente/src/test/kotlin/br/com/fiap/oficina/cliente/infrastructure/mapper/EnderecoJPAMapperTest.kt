package br.com.fiap.oficina.cliente.infrastructure.mapper

import br.com.fiap.oficina.cliente.ClienteFixtures
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.UUID

@DisplayName("Infraestrutura - Mapper JPA de Endereço")
class EnderecoJPAMapperTest {
    private val mapper = EnderecoJPAMapper()

    @Test
    @DisplayName("Dada uma entidade JPA, quando converter para domínio, então deve reconstruir o endereço")
    fun givenJpaEntity_whenConvertingToDomain_thenReconstructEndereco() {
        val endereco = mapper.toDomain(ClienteFixtures.enderecoJPA())

        assertEquals(ClienteFixtures.ENDERECO_ID, endereco.id.toString())
        assertEquals("Rua das Flores", endereco.logradouro)
        assertEquals("100", endereco.numero)
        assertEquals("Apto 42", endereco.complemento)
        assertEquals("Centro", endereco.bairro)
        assertEquals("São Paulo", endereco.cidade)
        assertEquals("SP", endereco.estado)
        assertEquals("01001000", endereco.cep)
    }

    @Test
    @DisplayName("Dada uma entidade JPA sem complemento, quando converter para domínio, então deve ser nulo")
    fun givenJpaEntityWithoutComplemento_whenConvertingToDomain_thenComplementoIsNull() {
        assertNull(mapper.toDomain(ClienteFixtures.enderecoJPA(complemento = null)).complemento)
    }

    @Test
    @DisplayName("Dado um endereço de domínio, quando converter para JPA, então deve preencher a entidade")
    fun givenDomainEndereco_whenConvertingToJpa_thenFillEntity() {
        val jpa = mapper.toJPA(ClienteFixtures.endereco())

        assertEquals(UUID.fromString(ClienteFixtures.ENDERECO_ID), jpa.id)
        assertEquals("Rua das Flores", jpa.logradouro)
        assertEquals("100", jpa.numero)
        assertEquals("Apto 42", jpa.complemento)
        assertEquals("Centro", jpa.bairro)
        assertEquals("São Paulo", jpa.cidade)
        assertEquals("SP", jpa.estado)
        assertEquals("01001000", jpa.cep)
    }

    @Test
    @DisplayName("Dada uma entidade JPA, quando converter ida e volta, então deve preservar os dados")
    fun givenJpaEntity_whenConvertingRoundTrip_thenPreserveData() {
        val jpa = ClienteFixtures.enderecoJPA(complemento = null)

        assertEquals(jpa, mapper.toJPA(mapper.toDomain(jpa)))
    }
}
