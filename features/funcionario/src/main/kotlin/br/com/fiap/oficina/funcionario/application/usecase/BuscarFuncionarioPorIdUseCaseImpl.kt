package br.com.fiap.oficina.funcionario.application.usecase

import br.com.fiap.oficina.funcionario.application.dto.FuncionarioResponse
import br.com.fiap.oficina.funcionario.application.mapper.FuncionarioMapper
import br.com.fiap.oficina.funcionario.domain.FuncionarioId
import br.com.fiap.oficina.funcionario.domain.FuncionarioRepository
import br.com.fiap.oficina.shared.domain.exception.EntityNotFoundException
import org.springframework.stereotype.Service

@Service
internal class BuscarFuncionarioPorIdUseCaseImpl(
    private val repository: FuncionarioRepository,
    private val mapper: FuncionarioMapper,
) : BuscarFuncionarioPorIdUseCase {
    override fun executar(id: String): FuncionarioResponse {
        val funcionario =
            repository.buscarPorId(FuncionarioId.toUUID(id))
                ?: throw EntityNotFoundException("Funcionário não encontrado, id: $id")

        return mapper.toResponse(funcionario)
    }
}
