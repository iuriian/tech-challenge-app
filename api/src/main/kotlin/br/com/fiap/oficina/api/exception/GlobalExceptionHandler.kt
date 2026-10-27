package br.com.fiap.oficina.api.exception

import br.com.fiap.oficina.shared.domain.exception.BusinessRuleException
import br.com.fiap.oficina.shared.domain.exception.ConflictException
import br.com.fiap.oficina.shared.domain.exception.DomainException
import br.com.fiap.oficina.shared.domain.exception.EntityNotFoundException
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.HandlerMethodValidationException
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.resource.NoResourceFoundException

@RestControllerAdvice
internal class GlobalExceptionHandler {
    private val log = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(EntityNotFoundException::class)
    fun naoEncontrado(
        ex: EntityNotFoundException,
        request: HttpServletRequest,
    ) = erro(HttpStatus.NOT_FOUND, "Recurso não encontrado", ex.message, request)

    @ExceptionHandler(NoResourceFoundException::class)
    fun rotaInexistente(
        ex: NoResourceFoundException,
        request: HttpServletRequest,
    ) = erro(HttpStatus.NOT_FOUND, "Rota não encontrada", "Nenhum recurso em ${request.requestURI}", request)

    @ExceptionHandler(ConflictException::class)
    fun conflito(
        ex: ConflictException,
        request: HttpServletRequest,
    ) = erro(HttpStatus.CONFLICT, "Conflito com o estado atual do recurso", ex.message, request)

    @ExceptionHandler(BusinessRuleException::class)
    fun regraDeNegocio(
        ex: BusinessRuleException,
        request: HttpServletRequest,
    ) = erro(HttpStatus.UNPROCESSABLE_ENTITY, "Regra de negócio violada", ex.message, request)

    @ExceptionHandler(DomainException::class)
    fun dominio(
        ex: DomainException,
        request: HttpServletRequest,
    ): ResponseEntity<ErrorResponse> {
        log.warn("DomainException sem handler específico: {}", ex::class.simpleName, ex)
        return erro(HttpStatus.UNPROCESSABLE_ENTITY, "Operação não permitida", ex.message, request)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun corpoInvalido(
        ex: MethodArgumentNotValidException,
        request: HttpServletRequest,
    ): ResponseEntity<ValidationErrorResponse> {
        val campos =
            ex.bindingResult.allErrors.associate { erro ->
                val campo = (erro as? FieldError)?.field ?: erro.objectName
                campo to (erro.defaultMessage ?: "valor inválido")
            }

        return ResponseEntity.badRequest().body(
            ValidationErrorResponse(
                status = HttpStatus.BAD_REQUEST.value(),
                error = "Dados inválidos",
                message = "Os dados fornecidos são inválidos",
                path = request.requestURI,
                fields = campos,
            ),
        )
    }

    @ExceptionHandler(HandlerMethodValidationException::class)
    fun parametroInvalido(
        ex: HandlerMethodValidationException,
        request: HttpServletRequest,
    ) = erro(HttpStatus.BAD_REQUEST, "Parâmetro inválido", "Parâmetros da requisição são inválidos", request)

    @ExceptionHandler(IllegalArgumentException::class)
    fun argumentoInvalido(
        ex: IllegalArgumentException,
        request: HttpServletRequest,
    ) = erro(HttpStatus.BAD_REQUEST, "Dados inválidos", ex.message, request)

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun tipoIncompativel(
        ex: MethodArgumentTypeMismatchException,
        request: HttpServletRequest,
    ) = erro(HttpStatus.BAD_REQUEST, "Parâmetro inválido", "Valor inválido para '${ex.name}'", request)

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun corpoIlegivel(
        ex: HttpMessageNotReadableException,
        request: HttpServletRequest,
    ) = erro(HttpStatus.BAD_REQUEST, "Corpo inválido", "O corpo da requisição não pôde ser lido", request)

    @ExceptionHandler(Exception::class)
    fun naoTratado(
        ex: Exception,
        request: HttpServletRequest,
    ): ResponseEntity<ErrorResponse> {
        log.error("Erro não tratado em {} {}", request.method, request.requestURI, ex)
        return erro(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Erro interno do servidor",
            "Ocorreu um erro inesperado. Contate o suporte.",
            request,
        )
    }

    private fun erro(
        status: HttpStatus,
        categoria: String,
        detalhe: String?,
        request: HttpServletRequest,
    ): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(status).body(
            ErrorResponse(
                status = status.value(),
                error = categoria,
                message = detalhe ?: categoria,
                path = request.requestURI,
            ),
        )
}
