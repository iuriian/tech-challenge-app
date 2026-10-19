package br.com.fiap.oficina.shared.domain.exception

class BusinessRuleException(
    message: String,
    cause: Throwable? = null,
) : DomainException(message, cause)
