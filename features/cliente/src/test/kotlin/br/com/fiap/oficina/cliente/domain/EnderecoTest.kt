package br.com.fiap.oficina.cliente.domain

import br.com.fiap.oficina.cliente.ClienteFixtures
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

@DisplayName("Domínio - Endereço")
class EnderecoTest {
    @Test
    @DisplayName("Dado dados válidos, quando criar endereço, então deve gerar um novo identificador")
    fun givenValidData_whenCreatingEndereco_thenGenerateNewId() {
        val endereco = criar()

        assertNotNull(endereco.id.value)
        assertEquals("Rua das Flores", endereco.logradouro)
        assertEquals("100", endereco.numero)
        assertEquals("Apto 42", endereco.complemento)
        assertEquals("Centro", endereco.bairro)
        assertEquals("São Paulo", endereco.cidade)
        assertEquals("SP", endereco.estado)
        assertEquals("01001000", endereco.cep)
    }

    @Test
    @DisplayName("Dado endereço sem complemento, quando criar endereço, então o complemento deve ser nulo")
    fun givenEnderecoWithoutComplemento_whenCreatingEndereco_thenComplementoIsNull() {
        assertNull(criar(complemento = null).complemento)
    }

    @Test
    @DisplayName("Dado um identificador existente, quando reconstruir endereço, então deve preservar o identificador")
    fun givenExistingId_whenReconstructingEndereco_thenPreserveId() {
        val endereco = ClienteFixtures.endereco()

        assertEquals(ClienteFixtures.ENDERECO_ID, endereco.id.toString())
        assertEquals("Centro", endereco.bairro)
    }

    @Test
    @DisplayName("Dado logradouro em branco, quando criar endereço, então deve lançar exceção")
    fun givenBlankLogradouro_whenCreatingEndereco_thenThrowException() {
        assertEquals("Logradouro é obrigatório", erroAoCriar(logradouro = " "))
    }

    @Test
    @DisplayName("Dado número em branco, quando criar endereço, então deve lançar exceção")
    fun givenBlankNumero_whenCreatingEndereco_thenThrowException() {
        assertEquals("Número é obrigatório", erroAoCriar(numero = ""))
    }

    @Test
    @DisplayName("Dado bairro em branco, quando criar endereço, então deve lançar exceção")
    fun givenBlankBairro_whenCreatingEndereco_thenThrowException() {
        assertEquals("Bairro é obrigatório", erroAoCriar(bairro = ""))
    }

    @Test
    @DisplayName("Dado cidade em branco, quando criar endereço, então deve lançar exceção")
    fun givenBlankCidade_whenCreatingEndereco_thenThrowException() {
        assertEquals("Cidade é obrigatória", erroAoCriar(cidade = " "))
    }

    @Test
    @DisplayName("Dado estado em branco, quando criar endereço, então deve lançar exceção")
    fun givenBlankEstado_whenCreatingEndereco_thenThrowException() {
        assertEquals("Estado é obrigatório", erroAoCriar(estado = ""))
    }

    @Test
    @DisplayName("Dado cep em branco, quando criar endereço, então deve lançar exceção")
    fun givenBlankCep_whenCreatingEndereco_thenThrowException() {
        assertEquals("CEP é obrigatório", erroAoCriar(cep = "  "))
    }

    private fun erroAoCriar(
        logradouro: String = "Rua das Flores",
        numero: String = "100",
        bairro: String = "Centro",
        cidade: String = "São Paulo",
        estado: String = "SP",
        cep: String = "01001000",
    ): String? = assertFailsWith<IllegalArgumentException> {
        criar(
            logradouro = logradouro,
            numero = numero,
            bairro = bairro,
            cidade = cidade,
            estado = estado,
            cep = cep,
        )
    }.message

    private fun criar(
        logradouro: String = "Rua das Flores",
        numero: String = "100",
        complemento: String? = "Apto 42",
        bairro: String = "Centro",
        cidade: String = "São Paulo",
        estado: String = "SP",
        cep: String = "01001000",
    ) = Endereco.criar(
        logradouro = logradouro,
        numero = numero,
        complemento = complemento,
        bairro = bairro,
        cidade = cidade,
        estado = estado,
        cep = cep,
    )
}
