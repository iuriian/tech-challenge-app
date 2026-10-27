package br.com.fiap.oficina.shared.domain.exception

class ConflictException(
    message: String,
    cause: Throwable? = null,
) : DomainException(message, cause)
