package br.com.fiap.oficina.funcionario.application.usecase

import br.com.fiap.oficina.funcionario.application.dto.FuncionarioResponse
import br.com.fiap.oficina.funcionario.application.mapper.FuncionarioMapper
import br.com.fiap.oficina.funcionario.domain.FuncionarioException
import br.com.fiap.oficina.funcionario.domain.FuncionarioId
import br.com.fiap.oficina.funcionario.domain.FuncionarioRepository
import org.springframework.stereotype.Service

@Service
internal class BuscarFuncionarioPorIdUseCaseImpl(
    private val repository: FuncionarioRepository,
    private val mapper: FuncionarioMapper,
) : BuscarFuncionarioPorIdUseCase {
    override fun execute(id: String): FuncionarioResponse {
        val funcionario =
            repository.buscarPorId(FuncionarioId.toUUID(id))
                ?: throw FuncionarioException("Funcionário não com o id $id encontrado!")

        return mapper.toResponse(funcionario)
    }
}
