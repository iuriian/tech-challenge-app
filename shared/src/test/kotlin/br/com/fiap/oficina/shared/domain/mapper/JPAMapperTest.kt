package br.com.fiap.oficina.shared.domain.mapper

import br.com.fiap.oficina.shared.domain.AggregateId
import br.com.fiap.oficina.shared.domain.valueobject.CPF
import br.com.fiap.oficina.shared.domain.valueobject.Document
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertFailsWith

@DisplayName("Shared kernel - Contrato do JPAMapper")
class JPAMapperTest {
    private data class ClienteId(override val value: UUID) : AggregateId()

    private data class Cliente(val id: ClienteId, val documento: Document)

    private data class ClienteJPA(val id: UUID, val documento: String)

    private class ClienteJPAMapper : JPAMapper<Cliente, ClienteJPA> {
        override fun toDomain(jpa: ClienteJPA): Cliente = Cliente(ClienteId(jpa.id), Document.from(jpa.documento))

        override fun toJPA(domain: Cliente): ClienteJPA = ClienteJPA(domain.id.value, domain.documento.valor)
    }

    private val mapper: JPAMapper<Cliente, ClienteJPA> = ClienteJPAMapper()

    @Test
    @DisplayName("Dada uma entidade JPA, quando converter para domínio, então deve montar o agregado")
    fun givenJPAEntity_whenConvertingToDomain_thenBuildAggregate() {
        val cliente = mapper.toDomain(ClienteJPA(UUID.fromString(ID_VALIDO), CPF_VALIDO))

        assertEquals(ClienteId(UUID.fromString(ID_VALIDO)), cliente.id)
        assertEquals(CPF(CPF_VALIDO), cliente.documento)
    }

    @Test
    @DisplayName("Dado um domínio, quando converter para JPA, então deve expor os valores primitivos")
    fun givenDomain_whenConvertingToJPA_thenExposePrimitiveValues() {
        val jpa = mapper.toJPA(Cliente(ClienteId(UUID.fromString(ID_VALIDO)), CPF(CPF_VALIDO)))

        assertEquals(UUID.fromString(ID_VALIDO), jpa.id)
        assertEquals(CPF_VALIDO, jpa.documento)
    }

    @Test
    @DisplayName("Dado um domínio, quando converter nos dois sentidos, então deve preservar o agregado")
    fun givenDomain_whenConvertingBothWays_thenPreserveAggregate() {
        val cliente = Cliente(ClienteId(UUID.fromString(ID_VALIDO)), CPF(CPF_VALIDO))

        assertEquals(cliente, mapper.toDomain(mapper.toJPA(cliente)))
    }

    @Test
    @DisplayName("Dada uma entidade JPA inválida, quando converter para domínio, então deve propagar a exceção")
    fun givenInvalidJPAEntity_whenConvertingToDomain_thenPropagateException() {
        val jpa = ClienteJPA(UUID.fromString(ID_VALIDO), "01234567891")

        val exception = assertFailsWith<IllegalArgumentException> { mapper.toDomain(jpa) }

        assertEquals("CPF inválido: 01234567891", exception.message)
    }

    private companion object {
        const val ID_VALIDO = "00000000-0000-0000-0000-000000000100"
        const val CPF_VALIDO = "01234567890"
    }
}
