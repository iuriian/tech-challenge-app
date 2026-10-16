package br.com.fiap.oficina.shared.domain.mapper

interface ApplicationMapper<Domain, Request, Response> {
    fun toDomain(request: Request): Domain

    fun toResponse(domain: Domain): Response
}
