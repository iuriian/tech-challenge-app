package br.com.fiap.oficina.shared.domain.mapper

interface CompositeApplicationMapper<Domain, Context, Request, Response> {
    fun toDomain(request: Request): Domain

    fun toResponse(
        domain: Domain,
        context: Context?,
    ): Response
}
