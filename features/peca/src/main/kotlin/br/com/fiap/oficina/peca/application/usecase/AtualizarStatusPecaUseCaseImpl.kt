package br.com.fiap.oficina.peca.application.usecase

import br.com.fiap.oficina.peca.application.dto.PecaResponse
import br.com.fiap.oficina.peca.application.dto.StatusPecaRequest
import br.com.fiap.oficina.peca.application.mapper.PecaMapper
import br.com.fiap.oficina.peca.domain.PecaRepository
import br.com.fiap.oficina.shared.domain.exception.EntityNotFoundException
import org.springframework.stereotype.Service

@Service
internal class AtualizarStatusPecaUseCaseImpl(private val repository: PecaRepository, private val mapper: PecaMapper) :
    AtualizarStatusPecaUseCase {
    override fun executar(codigo: String, status: StatusPecaRequest): PecaResponse {
        val peca =
            repository.buscarPorCodigo(codigo)
                ?: throw EntityNotFoundException("Peça não encontrada para $codigo")

        val pecaAtualizada =
            if (status.ativo) {
                peca.ativar()
            } else {
                peca.desativar()
            }

        return mapper.toResponse(repository.salvar(pecaAtualizada))
    }
}
