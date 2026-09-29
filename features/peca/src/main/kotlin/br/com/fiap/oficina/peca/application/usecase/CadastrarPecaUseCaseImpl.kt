package br.com.fiap.oficina.peca.application.usecase

import br.com.fiap.oficina.peca.application.dto.PecaRequest
import br.com.fiap.oficina.peca.application.dto.PecaResponse
import br.com.fiap.oficina.peca.application.mapper.PecaMapper
import br.com.fiap.oficina.peca.domain.PecaRepository
import org.springframework.stereotype.Service

@Service
internal class CadastrarPecaUseCaseImpl(
    private val repository: PecaRepository,
    private val mapper: PecaMapper,
) : CadastrarPecaUseCase {
    override fun executar(request: PecaRequest): PecaResponse {
        repository.buscarPorCodigo(request.codigo)?.let {
            throw IllegalArgumentException("Peça já cadastrada")
        }

        val peca = mapper.toDomain(request)

        return mapper.toResponse(repository.salvar(peca))
    }
}
