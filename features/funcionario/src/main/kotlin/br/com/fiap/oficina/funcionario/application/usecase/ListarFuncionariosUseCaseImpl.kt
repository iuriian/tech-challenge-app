package br.com.fiap.oficina.funcionario.application.usecase

import br.com.fiap.oficina.funcionario.application.dto.FuncionarioResponse
import br.com.fiap.oficina.funcionario.application.mapper.FuncionarioMapper
import br.com.fiap.oficina.funcionario.domain.FuncionarioRepository
import org.springframework.stereotype.Service

@Service
internal class ListarFuncionariosUseCaseImpl(
    private val repository: FuncionarioRepository,
    private val mapper: FuncionarioMapper,
) : ListarFuncionariosUseCase {
    override fun executar(): List<FuncionarioResponse> {
        val funcionarios = repository.listarTodos()

        return funcionarios.map { mapper.toResponse(it) }
    }
}
