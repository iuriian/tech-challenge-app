package br.com.fiap.oficina.peca.application.usecase

import br.com.fiap.oficina.peca.application.dto.PecaRequest
import br.com.fiap.oficina.peca.application.dto.PecaResponse
import br.com.fiap.oficina.peca.application.mapper.PecaMapper
import br.com.fiap.oficina.peca.domain.Peca
import br.com.fiap.oficina.peca.domain.PecaId
import br.com.fiap.oficina.peca.domain.PecaRepository
import org.springframework.stereotype.Service

@Service
internal class AtualizarPecaUseCaseImpl(
    private val repository: PecaRepository,
    private val mapper: PecaMapper,
) : AtualizarPecaUseCase {
    override fun executar(
        id: String,
        request: PecaRequest,
    ): PecaResponse {
        require(id.isNotBlank()) { "Id é obrigatório!" }

        val peca =
            repository.buscarPorId(PecaId.toUUID(id))
                ?: throw IllegalArgumentException("Peça não encontrada!")

        validarCodigo(request.codigo, peca)

        val pecaAtualizada =
            peca.atualizar(
                nome = request.nome,
                codigo = request.codigo,
                descricao = request.descricao,
                fabricante = request.fabricante,
                fornecedor = request.fornecedor,
                precoCompra = request.precoDeCompra,
                precoVenda = request.precoDeVenda,
            )

        return mapper.toResponse(repository.salvar(pecaAtualizada))
    }

    private fun validarCodigo(
        codigo: String,
        peca: Peca,
    ) {
        if (codigo == peca.codigo) return

        val pecaCadastrada = repository.buscarPorCodigo(codigo)

        if (pecaCadastrada != null && pecaCadastrada.id != peca.id) {
            throw IllegalArgumentException("Código já cadastrado!")
        }
    }
}
