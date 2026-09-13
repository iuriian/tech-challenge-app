package br.com.fiap.oficina.funcionario.application.usecase

import br.com.fiap.oficina.funcionario.application.dto.FuncionarioResponse
import br.com.fiap.oficina.funcionario.application.mapper.FuncionarioMapper
import br.com.fiap.oficina.funcionario.domain.FuncionarioException
import br.com.fiap.oficina.funcionario.domain.FuncionarioRepository
import org.springframework.stereotype.Service

@Service
internal class BuscarFuncionarioPorNomeUseCaseImpl(
    private val repository: FuncionarioRepository,
    private val mapper: FuncionarioMapper,
) : BuscarFuncionarioPorNomeUseCase {
    override fun executar(nome: String): FuncionarioResponse {
        val funcionario =
            repository.buscarPorNome(nome)
                ?: throw FuncionarioException("Funcionário com nome $nome não encontrado")

        return mapper.toResponse(funcionario)
    }
}
