package br.com.fiap.oficina.shared.domain.mapper

interface QueryMapper<Domain, QueryResponseDto> {
    fun toProviderResponse(domain: Domain): QueryResponseDto
}
