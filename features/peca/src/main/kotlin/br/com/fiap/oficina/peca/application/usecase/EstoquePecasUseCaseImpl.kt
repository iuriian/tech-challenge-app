package br.com.fiap.oficina.peca.application.usecase

import br.com.fiap.oficina.peca.application.dto.EstoquePecaRequest
import br.com.fiap.oficina.peca.application.dto.PecaResponse
import br.com.fiap.oficina.peca.application.mapper.PecaMapper
import br.com.fiap.oficina.peca.domain.PecaRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.math.abs

@Service
internal class EstoquePecasUseCaseImpl(
    private val repository: PecaRepository,
    private val mapper: PecaMapper,
) : EstoquePecasUseCase {
    @Transactional
    override fun executar(
        codigo: String,
        request: EstoquePecaRequest,
    ): PecaResponse {
        require(request.quantidade != 0) { "Valor inválido para atualização de estoque" }

        val peca =
            repository.buscarPorCodigo(codigo)
                ?: throw IllegalArgumentException("Peça não encontrada para o estoque")

        val estoqueAtualizado =
            if (request.quantidade < 0) {
                require((peca.quantidade - abs(request.quantidade)) >= 0) {
                    "Quantidade em estoque insuficiente"
                }

                peca.retirarPecas(abs(request.quantidade))
            } else {
                peca.reporPecas(request.quantidade)
            }

        return mapper.toResponse(repository.salvar(estoqueAtualizado))
    }
}
