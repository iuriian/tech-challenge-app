package br.com.fiap.oficina.servico.application.usecase

import br.com.fiap.oficina.servico.application.dto.ServicoResponse
import br.com.fiap.oficina.servico.application.mapper.ServicoMapper
import br.com.fiap.oficina.servico.domain.ServicoId
import br.com.fiap.oficina.servico.domain.ServicoRepository
import br.com.fiap.oficina.shared.domain.exception.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
internal class ReativarServicoUseCaseImpl(
    private val repository: ServicoRepository,
    private val mapper: ServicoMapper,
) : ReativarServicoUseCase {
    @Transactional
    override fun executar(id: String): ServicoResponse {
        val servico =
            repository.buscarPorId(ServicoId.toUUID(id))
                ?: throw EntityNotFoundException("Serviço não encontrado com o id: $id")

        return mapper.toResponse(repository.salvar(servico.reativar()))
    }
}
