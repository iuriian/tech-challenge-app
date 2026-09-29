package br.com.fiap.oficina.peca.application.usecase

import br.com.fiap.oficina.peca.application.dto.PecaResponse
import br.com.fiap.oficina.peca.application.mapper.PecaMapper
import br.com.fiap.oficina.peca.domain.Peca
import br.com.fiap.oficina.peca.domain.PecaRepository
import org.springframework.stereotype.Service

@Service
internal class BuscarPecaPorCodigoUseCaseImpl(
    private val repository: PecaRepository,
    private val mapper: PecaMapper,
) : BuscarPecaPorCodigoUseCase {
    override fun executar(codigo: String): PecaResponse {
        val peca =
            repository.buscarPorCodigo(codigo)
                ?: throw IllegalArgumentException("Peça não encontrada! \nCódigo desconhecido.")

        return mapper.toResponse(peca)
    }
}
