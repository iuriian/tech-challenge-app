package br.com.fiap.oficina.funcionario.application.mapper

import br.com.fiap.oficina.funcionario.application.dto.FuncionarioRequest
import br.com.fiap.oficina.funcionario.domain.Cargo
import br.com.fiap.oficina.funcionario.domain.Funcionario
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertFailsWith

@DisplayName("Mapper - Funcionário")
class FuncionarioMapperTest {
    private val mapper = FuncionarioMapper()

    @Test
    @DisplayName("Dado request sem id, quando converter para domínio, então deve criar um novo funcionário")
    fun givenRequestWithoutId_whenConvertingToDomain_thenCreateNewFuncionario() {
        val request =
            FuncionarioRequest(
                nome = "João Silva",
                cargo = "MECANICO",
                cpf = CPF_VALIDO,
            )

        val funcionario = mapper.toDomain(request)

        assertNotNull(funcionario.id.value)
        assertEquals("João Silva", funcionario.nome)
        assertEquals(Cargo.MECANICO, funcionario.cargo)
        assertEquals(CPF_VALIDO, funcionario.cpf.value)
    }

    @Test
    @DisplayName("Dado request com id, quando converter para domínio, então deve reconstruir preservando o id")
    fun givenRequestWithId_whenConvertingToDomain_thenReconstructPreservingId() {
        val request =
            FuncionarioRequest(
                id = ID_VALIDO,
                nome = "Maria Souza",
                cargo = "ATENDENTE",
                cpf = CPF_VALIDO,
            )

        val funcionario = mapper.toDomain(request)

        assertEquals(UUID.fromString(ID_VALIDO), funcionario.id.value)
        assertEquals("Maria Souza", funcionario.nome)
        assertEquals(Cargo.ATENDENTE, funcionario.cargo)
        assertEquals(CPF_VALIDO, funcionario.cpf.value)
    }

    @Test
    @DisplayName("Dado request com cargo inválido, quando converter para domínio, então deve lançar exceção")
    fun givenRequestWithInvalidCargo_whenConvertingToDomain_thenThrowException() {
        val request =
            FuncionarioRequest(
                nome = "João Silva",
                cargo = "CARGO_INEXISTENTE",
                cpf = CPF_VALIDO,
            )

        val exception =
            assertFailsWith<IllegalArgumentException> {
                mapper.toDomain(request)
            }

        assertEquals("Cargo inválido!", exception.message)
    }

    @Test
    @DisplayName("Dado request com cpf inválido, quando converter para domínio, então deve lançar exceção")
    fun givenRequestWithInvalidCpf_whenConvertingToDomain_thenThrowException() {
        val request =
            FuncionarioRequest(
                nome = "João Silva",
                cargo = "MECANICO",
                cpf = "123",
            )

        val exception =
            assertFailsWith<IllegalArgumentException> {
                mapper.toDomain(request)
            }

        assertEquals("CPF deve conter 11 dígitos", exception.message)
    }

    @Test
    @DisplayName("Dado um funcionário mecânico, quando converter para response, então deve expor a descrição do cargo")
    fun givenMecanicoFuncionario_whenConvertingToResponse_thenExposeCargoDescricao() {
        val funcionario =
            Funcionario.reconstruir(
                id = ID_VALIDO,
                nome = "Maria Souza",
                cargo = "MECANICO",
                cpf = CPF_VALIDO,
            )

        val response = mapper.toResponse(funcionario)

        assertEquals(ID_VALIDO, response.id)
        assertEquals("Maria Souza", response.nome)
        assertEquals(CPF_VALIDO, response.cpf)
        assertEquals("Mecânico", response.cargoDescricao)
    }

    @Test
    @DisplayName("Dado um funcionário atendente, quando converter para response, então deve expor a descrição do cargo")
    fun givenAtendenteFuncionario_whenConvertingToResponse_thenExposeCargoDescricao() {
        val funcionario =
            Funcionario.reconstruir(
                id = ID_VALIDO,
                nome = "João Silva",
                cargo = "ATENDENTE",
                cpf = CPF_VALIDO,
            )

        val response = mapper.toResponse(funcionario)

        assertEquals("Atendente", response.cargoDescricao)
    }

    @Test
    @DisplayName("Dado um request com id, quando converter ida e volta, então deve preservar os dados")
    fun givenRequestWithId_whenConvertingRoundTrip_thenPreserveData() {
        val request =
            FuncionarioRequest(
                id = ID_VALIDO,
                nome = "Maria Souza",
                cargo = "ATENDENTE",
                cpf = CPF_VALIDO,
            )

        val response = mapper.toResponse(mapper.toDomain(request))

        assertEquals(request.id, response.id)
        assertEquals(request.nome, response.nome)
        assertEquals(request.cpf, response.cpf)
        assertEquals(Cargo.ATENDENTE.descricao, response.cargoDescricao)
    }

    private companion object {
        const val ID_VALIDO = "00000000-0000-0000-0000-000000000100"
        const val CPF_VALIDO = "01234567890"
    }
}
