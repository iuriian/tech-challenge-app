package br.com.fiap.oficina.cliente.infrastructure.mapper

import br.com.fiap.oficina.cliente.ClienteFixtures
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.UUID

@DisplayName("Infraestrutura - Mapper JPA de Contato")
class ContatoJPAMapperTest {
    private val mapper = ContatoJPAMapper()

    @Test
    @DisplayName("Dada uma entidade JPA, quando converter para domínio, então deve reconstruir o contato")
    fun givenJpaEntity_whenConvertingToDomain_thenReconstructContato() {
        val contato = mapper.toDomain(ClienteFixtures.contatoJPA())

        assertEquals(ClienteFixtures.CONTATO_ID, contato.id.toString())
        assertEquals("CELULAR", contato.tipo)
        assertEquals("João Silva", contato.nome)
        assertEquals("11999998888", contato.telefone)
    }

    @Test
    @DisplayName("Dado um contato de domínio, quando converter para JPA, então deve preencher a entidade")
    fun givenDomainContato_whenConvertingToJpa_thenFillEntity() {
        val jpa = mapper.toJPA(ClienteFixtures.contato(tipo = "COMERCIAL"))

        assertEquals(UUID.fromString(ClienteFixtures.CONTATO_ID), jpa.id)
        assertEquals("COMERCIAL", jpa.tipo)
        assertEquals("João Silva", jpa.nome)
        assertEquals("11999998888", jpa.telefone)
    }

    @Test
    @DisplayName("Dada uma entidade JPA, quando converter ida e volta, então deve preservar os dados")
    fun givenJpaEntity_whenConvertingRoundTrip_thenPreserveData() {
        val jpa = ClienteFixtures.contatoJPA()

        assertEquals(jpa, mapper.toJPA(mapper.toDomain(jpa)))
    }
}
