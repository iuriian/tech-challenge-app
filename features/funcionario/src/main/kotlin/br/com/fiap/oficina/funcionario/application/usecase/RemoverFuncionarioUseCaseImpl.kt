package br.com.fiap.oficina.funcionario.application.usecase

import br.com.fiap.oficina.funcionario.domain.FuncionarioException
import br.com.fiap.oficina.funcionario.domain.FuncionarioId
import br.com.fiap.oficina.funcionario.domain.FuncionarioRepository
import org.springframework.stereotype.Service

@Service
internal class RemoverFuncionarioUseCaseImpl(private val repository: FuncionarioRepository) :
    RemoverFuncionarioUseCase {
    override fun executar(id: String) {
        val funcionario =
            repository.buscarPorId(FuncionarioId.toUUID(id))
                ?: throw FuncionarioException("Funcionário não encontrado com o ID: $id")

        repository.deletar(funcionario.id)
    }
}
