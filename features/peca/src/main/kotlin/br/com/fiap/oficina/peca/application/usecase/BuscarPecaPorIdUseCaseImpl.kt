package br.com.fiap.oficina.peca.application.usecase

import br.com.fiap.oficina.peca.application.dto.PecaResponse
import br.com.fiap.oficina.peca.application.mapper.PecaMapper
import br.com.fiap.oficina.peca.domain.PecaId
import br.com.fiap.oficina.peca.domain.PecaRepository
import org.springframework.stereotype.Service

@Service
internal class BuscarPecaPorIdUseCaseImpl(
    private val repository: PecaRepository,
    private val mapper: PecaMapper
): BuscarPecaPorIdUseCase {
    override fun executar(id: String): PecaResponse {
        val peca = repository.buscarPorId(PecaId.toUUID(id))
            ?: throw IllegalArgumentException("Peça não encontrada, id: $id")

        return mapper.toResponse(peca)
    }
}
