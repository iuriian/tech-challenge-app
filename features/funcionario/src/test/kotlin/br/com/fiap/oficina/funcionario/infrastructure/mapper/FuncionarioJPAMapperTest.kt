package br.com.fiap.oficina.funcionario.infrastructure.mapper

import br.com.fiap.oficina.funcionario.domain.Cargo
import br.com.fiap.oficina.funcionario.domain.Funcionario
import br.com.fiap.oficina.funcionario.infrastructure.persistence.FuncionarioJPA
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import java.util.UUID
import kotlin.test.assertFailsWith

@DisplayName("Infraestrutura - Mapper JPA de Funcionário")
class FuncionarioJPAMapperTest {
    private val mapper = FuncionarioJPAMapper()

    @Test
    @DisplayName("Dado um funcionário de domínio, quando converter para JPA, então deve mapear todos os campos")
    fun givenDomainFuncionario_whenConvertingToJPA_thenMapAllFields() {
        val funcionario =
            Funcionario.reconstruir(
                id = ID_VALIDO,
                nome = "João Silva",
                cpf = CPF_VALIDO,
                cargo = "Mecânico",
            )

        val jpa = mapper.toJPA(funcionario)

        assertEquals(UUID.fromString(ID_VALIDO), jpa.id)
        assertEquals("João Silva", jpa.nome)
        assertEquals(CPF_VALIDO, jpa.cpf)
        assertEquals(Cargo.MECANICO, jpa.cargo)
    }

    @ParameterizedTest(name = "cargo {0}")
    @EnumSource(Cargo::class)
    @DisplayName("Dado uma entidade JPA, quando converter para domínio, então deve mapear todos os campos")
    fun givenJPAEntity_whenConvertingToDomain_thenMapAllFields(cargo: Cargo) {
        val jpa =
            FuncionarioJPA(
                id = UUID.fromString(ID_VALIDO),
                nome = "Maria Souza",
                cargo = cargo,
                cpf = CPF_VALIDO,
            )

        val funcionario = mapper.toDomain(jpa)

        assertEquals(UUID.fromString(ID_VALIDO), funcionario.id.value)
        assertEquals("Maria Souza", funcionario.nome)
        assertEquals(CPF_VALIDO, funcionario.cpf.value)
        assertEquals(cargo, funcionario.cargo)
    }

    @Test
    @DisplayName("Dado um funcionário de domínio, quando converter ida e volta, então deve preservar a igualdade")
    fun givenDomainFuncionario_whenConvertingRoundTrip_thenPreserveEquality() {
        val funcionario = Funcionario.criar(nome = "Ana Oliveira", cpf = CPF_VALIDO, cargo = "Atendente")

        assertEquals(funcionario, mapper.toDomain(mapper.toJPA(funcionario)))
    }

    @Test
    @DisplayName("Dado uma entidade JPA com cpf inválido, quando converter para domínio, então deve lançar exceção")
    fun givenJPAEntityWithInvalidCpf_whenConvertingToDomain_thenThrowException() {
        val jpa =
            FuncionarioJPA(
                id = UUID.fromString(ID_VALIDO),
                nome = "Maria Souza",
                cargo = Cargo.ATENDENTE,
                cpf = "123",
            )

        val exception =
            assertFailsWith<IllegalArgumentException> {
                mapper.toDomain(jpa)
            }

        assertEquals("CPF deve conter 11 dígitos", exception.message)
    }

    @Test
    @DisplayName("Dado uma entidade JPA com nome em branco, quando converter para domínio, então deve lançar exceção")
    fun givenJPAEntityWithBlankNome_whenConvertingToDomain_thenThrowException() {
        val jpa =
            FuncionarioJPA(
                id = UUID.fromString(ID_VALIDO),
                nome = "   ",
                cargo = Cargo.ATENDENTE,
                cpf = CPF_VALIDO,
            )

        val exception =
            assertFailsWith<IllegalArgumentException> {
                mapper.toDomain(jpa)
            }

        assertEquals("Nome não pode ser vazio", exception.message)
    }

    private companion object {
        const val ID_VALIDO = "00000000-0000-0000-0000-000000000100"
        const val CPF_VALIDO = "01234567890"
    }
}
