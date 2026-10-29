package br.com.fiap.oficina.api.exception

import br.com.fiap.oficina.shared.domain.exception.BusinessRuleException
import br.com.fiap.oficina.shared.domain.exception.ConflictException
import br.com.fiap.oficina.shared.domain.exception.DomainException
import br.com.fiap.oficina.shared.domain.exception.EntityNotFoundException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.core.MethodParameter
import org.springframework.http.HttpStatus
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

/** Exceção de domínio sem handler dedicado, usada para exercitar o fallback de [DomainException]. */
private class ExcecaoDeDominioGenerica(mensagem: String) : DomainException(mensagem)

/** Alvo apenas para obter um [MethodParameter] real nas exceções do Spring. */
private class Alvo {
    @Suppress("UNUSED_PARAMETER")
    fun metodo(valor: String): String = valor
}

@DisplayName("Exception handler global")
class GlobalExceptionHandlerTest {
    private val handler = GlobalExceptionHandler()

    private fun request(metodo: String = "GET", uri: String = "/api/v2/clientes/42") =
        MockHttpServletRequest(metodo, uri)

    private fun parametro() = MethodParameter(Alvo::class.java.getDeclaredMethod("metodo", String::class.java), 0)

    @Test
    @DisplayName("Dado uma entidade inexistente, quando tratada, entao deve responder 404")
    fun givenEntityNotFound_whenHandled_thenRespond404() {
        val resposta = handler.naoEncontrado(EntityNotFoundException("Cliente não encontrado"), request())

        assertEquals(HttpStatus.NOT_FOUND, resposta.statusCode)
        assertEquals(404, resposta.body?.status)
        assertEquals("Recurso não encontrado", resposta.body?.error)
        assertEquals("Cliente não encontrado", resposta.body?.message)
        assertEquals("/api/v2/clientes/42", resposta.body?.path)
        assertNotNull(resposta.body?.timestamp)
    }

    @Test
    @DisplayName("Dado uma rota inexistente, quando tratada, entao deve responder 404 citando o caminho")
    fun givenUnknownRoute_whenHandled_thenRespond404WithPath() {
        val resposta = handler.rotaInexistente(request(uri = "/api/v2/nao-existe"))

        assertEquals(HttpStatus.NOT_FOUND, resposta.statusCode)
        assertEquals("Rota não encontrada", resposta.body?.error)
        assertEquals("Nenhum recurso em /api/v2/nao-existe", resposta.body?.message)
    }

    @Test
    @DisplayName("Dado um conflito de estado, quando tratado, entao deve responder 409")
    fun givenConflict_whenHandled_thenRespond409() {
        val resposta = handler.conflito(ConflictException("Peça já cadastrada"), request(metodo = "POST"))

        assertEquals(HttpStatus.CONFLICT, resposta.statusCode)
        assertEquals(409, resposta.body?.status)
        assertEquals("Conflito com o estado atual do recurso", resposta.body?.error)
    }

    @Test
    @DisplayName("Dado uma regra de negocio violada, quando tratada, entao deve responder 422")
    fun givenBusinessRuleViolation_whenHandled_thenRespond422() {
        val resposta = handler.regraDeNegocio(BusinessRuleException("Cargo inválido"), request(metodo = "POST"))

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, resposta.statusCode)
        assertEquals(422, resposta.body?.status)
        assertEquals("Regra de negócio violada", resposta.body?.error)
        assertEquals("Cargo inválido", resposta.body?.message)
    }

    @Test
    @DisplayName("Dado uma excecao de dominio sem handler proprio, quando tratada, entao deve responder 422")
    fun givenDomainExceptionWithoutDedicatedHandler_whenHandled_thenRespond422() {
        val resposta = handler.dominio(ExcecaoDeDominioGenerica("Operação bloqueada"), request(metodo = "PUT"))

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, resposta.statusCode)
        assertEquals("Operação não permitida", resposta.body?.error)
        assertEquals("Operação bloqueada", resposta.body?.message)
    }

    @Test
    @DisplayName("Dado erros de campo e de objeto, quando o corpo e invalido, entao deve responder 400 por campo")
    fun givenFieldAndObjectErrors_whenBodyIsInvalid_thenRespond400PerField() {
        val bindingResult = BeanPropertyBindingResult("alvo", "funcionarioRequest")
        bindingResult.addError(FieldError("funcionarioRequest", "nome", "não deve estar em branco"))
        bindingResult.addError(ObjectError("funcionarioRequest", null))

        val resposta =
            handler.corpoInvalido(
                MethodArgumentNotValidException(parametro(), bindingResult),
                request(metodo = "POST", uri = "/api/v2/funcionarios"),
            )

        assertEquals(HttpStatus.BAD_REQUEST, resposta.statusCode)
        assertEquals(400, resposta.body?.status)
        assertEquals("Dados inválidos", resposta.body?.error)
        assertEquals("Os dados fornecidos são inválidos", resposta.body?.message)
        assertEquals("/api/v2/funcionarios", resposta.body?.path)
        assertEquals("não deve estar em branco", resposta.body?.fields?.get("nome"))
        assertEquals("valor inválido", resposta.body?.fields?.get("funcionarioRequest"))
    }

    @Test
    @DisplayName("Dado parametros invalidos de requisicao, quando tratados, entao deve responder 400")
    fun givenInvalidRequestParameters_whenHandled_thenRespond400() {
        val resposta = handler.parametroInvalido(request())

        assertEquals(HttpStatus.BAD_REQUEST, resposta.statusCode)
        assertEquals("Parâmetro inválido", resposta.body?.error)
        assertEquals("Parâmetros da requisição são inválidos", resposta.body?.message)
    }

    @Test
    @DisplayName("Dado um argumento invalido com mensagem, quando tratado, entao deve responder 400 com a mensagem")
    fun givenIllegalArgumentWithMessage_whenHandled_thenRespond400WithMessage() {
        val resposta = handler.argumentoInvalido(IllegalArgumentException("CPF inválido"), request())

        assertEquals(HttpStatus.BAD_REQUEST, resposta.statusCode)
        assertEquals("CPF inválido", resposta.body?.message)
    }

    @Test
    @DisplayName("Dado um argumento invalido sem mensagem, quando tratado, entao deve repetir a categoria")
    fun givenIllegalArgumentWithoutMessage_whenHandled_thenFallBackToCategory() {
        val resposta = handler.argumentoInvalido(IllegalArgumentException(), request())

        assertEquals(HttpStatus.BAD_REQUEST, resposta.statusCode)
        assertEquals("Dados inválidos", resposta.body?.error)
        assertEquals("Dados inválidos", resposta.body?.message)
    }

    @Test
    @DisplayName("Dado um tipo incompativel de parametro, quando tratado, entao deve responder 400 citando o nome")
    fun givenTypeMismatch_whenHandled_thenRespond400WithParameterName() {
        val excecao =
            MethodArgumentTypeMismatchException(
                "abc",
                Int::class.java,
                "qtd",
                parametro(),
                NumberFormatException("For input string: abc"),
            )

        val resposta = handler.tipoIncompativel(excecao, request(metodo = "PATCH"))

        assertEquals(HttpStatus.BAD_REQUEST, resposta.statusCode)
        assertEquals("Parâmetro inválido", resposta.body?.error)
        assertEquals("Valor inválido para 'qtd'", resposta.body?.message)
    }

    @Test
    @DisplayName("Dado um corpo ilegivel, quando tratado, entao deve responder 400")
    fun givenUnreadableBody_whenHandled_thenRespond400() {
        val resposta = handler.corpoIlegivel(request(metodo = "POST", uri = "/api/v2/clientes"))

        assertEquals(HttpStatus.BAD_REQUEST, resposta.statusCode)
        assertEquals("Corpo inválido", resposta.body?.error)
        assertEquals("O corpo da requisição não pôde ser lido", resposta.body?.message)
    }

    @Test
    @DisplayName("Dado uma excecao inesperada, quando tratada, entao deve responder 500 sem vazar o detalhe")
    fun givenUnexpectedException_whenHandled_thenRespond500WithoutLeakingDetail() {
        val resposta = handler.naoTratado(RuntimeException("NullPointer no repositório"), request())

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resposta.statusCode)
        assertEquals(500, resposta.body?.status)
        assertEquals("Erro interno do servidor", resposta.body?.error)
        assertEquals("Ocorreu um erro inesperado. Contate o suporte.", resposta.body?.message)
        assertTrue(resposta.body?.message?.contains("NullPointer") == false)
    }
}
