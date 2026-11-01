package br.com.fiap.oficina.servico.application.usecase

import br.com.fiap.oficina.servico.application.dto.ServicoResponse
import br.com.fiap.oficina.servico.application.mapper.ServicoMapper
import br.com.fiap.oficina.servico.domain.ServicoId
import br.com.fiap.oficina.servico.domain.ServicoRepository
import br.com.fiap.oficina.shared.domain.exception.EntityNotFoundException
import org.springframework.stereotype.Service

@Service
internal class BuscarServicoUseCaseImpl(private val repository: ServicoRepository, private val mapper: ServicoMapper) :
    BuscarServicoUseCase {
    override fun executar(id: String): ServicoResponse {
        val servico =
            repository.buscarPorId(ServicoId.toUUID(id))
                ?: throw EntityNotFoundException("Serviço não encontrado com o id: $id")

        return mapper.toResponse(servico)
    }
}
