package br.com.fiap.oficina.shared.domain.exception

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Shared kernel - Exceções de domínio")
class DomainExceptionTest {
    @Test
    @DisplayName("Dada uma entidade inexistente, quando lançar a exceção, então deve preservar a mensagem")
    fun givenMissingEntity_whenThrowing_thenPreserveMessage() {
        val exception = EntityNotFoundException("Peça não encontrada, id: 1")

        assertEquals("Peça não encontrada, id: 1", exception.message)
        assertNull(exception.cause)
    }

    @Test
    @DisplayName("Dada uma regra violada, quando lançar a exceção, então deve preservar a mensagem")
    fun givenBrokenRule_whenThrowing_thenPreserveMessage() {
        val exception = BusinessRuleException("Peça já cadastrada")

        assertEquals("Peça já cadastrada", exception.message)
        assertNull(exception.cause)
    }

    @Test
    @DisplayName("Dadas as exceções de domínio, quando verificar o tipo, então devem ser DomainException")
    fun givenDomainExceptions_whenCheckingType_thenShouldBeDomainException() {
        assertInstanceOf(DomainException::class.java, EntityNotFoundException("não encontrada"))
        assertInstanceOf(DomainException::class.java, BusinessRuleException("regra violada"))
    }

    @Test
    @DisplayName("Dada uma causa original, quando encapsular, então deve preservar a causa")
    fun givenRootCause_whenWrapping_thenPreserveCause() {
        val causa = IllegalStateException("falha original")
        val exception = object : DomainException("falhou", causa) {}

        assertSame(causa, exception.cause)
        assertEquals("falhou", exception.message)
    }
}
