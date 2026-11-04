package br.com.fiap.oficina.shared.domain.mapper

interface QueryMapper<DTO, QueryResponseDto> {
    fun toProviderResponse(dto: DTO): QueryResponseDto
}
