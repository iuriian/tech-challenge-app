package br.com.fiap.oficina.peca.application.usecase

import br.com.fiap.oficina.peca.application.dto.PecaRequest
import br.com.fiap.oficina.peca.application.dto.PecaResponse
import br.com.fiap.oficina.peca.application.mapper.PecaMapper
import br.com.fiap.oficina.peca.domain.PecaRepository
import br.com.fiap.oficina.shared.domain.exception.BusinessRuleException
import org.springframework.stereotype.Service

@Service
internal class CadastrarPecaUseCaseImpl(private val repository: PecaRepository, private val mapper: PecaMapper) :
    CadastrarPecaUseCase {
    override fun executar(request: PecaRequest): PecaResponse {
        repository.buscarPorCodigo(request.codigo)?.let {
            throw BusinessRuleException("Peça já cadastrada")
        }

        val peca = mapper.toDomain(request)

        return mapper.toResponse(repository.salvar(peca))
    }
}
