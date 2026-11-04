package br.com.fiap.oficina.servico.application.usecase

import br.com.fiap.oficina.servico.application.dto.ServicoRequest
import br.com.fiap.oficina.servico.application.dto.ServicoResponse
import br.com.fiap.oficina.servico.application.mapper.ServicoMapper
import br.com.fiap.oficina.servico.domain.ServicoRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
internal class CriarServicoUseCaseImpl(private val repository: ServicoRepository, private val mapper: ServicoMapper) :
    CriarServicoUseCase {
    @Transactional
    override fun executar(request: ServicoRequest): ServicoResponse {
        val servico = mapper.toDomain(request)

        return mapper.toResponse(repository.salvar(servico))
    }
}
