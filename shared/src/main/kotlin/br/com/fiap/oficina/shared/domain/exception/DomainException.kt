package br.com.fiap.oficina.shared.domain.exception

abstract class DomainException(
    message: String,
    cause: Throwable? = null,
) : RuntimeException(message, cause)
