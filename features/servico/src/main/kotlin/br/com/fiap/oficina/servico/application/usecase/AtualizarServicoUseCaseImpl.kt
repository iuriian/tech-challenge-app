package br.com.fiap.oficina.servico.application.usecase

import br.com.fiap.oficina.servico.application.dto.ServicoRequest
import br.com.fiap.oficina.servico.application.dto.ServicoResponse
import br.com.fiap.oficina.servico.application.mapper.ServicoMapper
import br.com.fiap.oficina.servico.domain.ServicoId
import br.com.fiap.oficina.servico.domain.ServicoRepository
import br.com.fiap.oficina.shared.domain.exception.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
internal class AtualizarServicoUseCaseImpl(
    private val repository: ServicoRepository,
    private val mapper: ServicoMapper,
) : AtualizarServicoUseCase {
    @Transactional
    override fun executar(id: String, request: ServicoRequest): ServicoResponse {
        val servico =
            repository.buscarPorId(ServicoId.toUUID(id))
                ?: throw EntityNotFoundException("Serviço não encontrado com o id: $id")

        val atualizado =
            servico
                .alterarDescricao(request.descricao)
                .alterarValor(request.valor)

        return mapper.toResponse(repository.salvar(atualizado))
    }
}
