package br.com.fiap.oficina.peca.application.usecase

import br.com.fiap.oficina.peca.application.dto.PecaResponse
import br.com.fiap.oficina.peca.application.mapper.PecaMapper
import br.com.fiap.oficina.peca.domain.PecaRepository
import org.springframework.stereotype.Service

@Service
internal class ListarPecasUseCaseImpl(private val repository: PecaRepository, private val mapper: PecaMapper) :
    ListarPecasUseCase {
    override fun executar(): List<PecaResponse> {
        val pecas = repository.listar()

        return pecas.map(mapper::toResponse)
    }
}
