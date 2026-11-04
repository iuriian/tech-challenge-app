package br.com.fiap.oficina.funcionario.application.usecase

import br.com.fiap.oficina.funcionario.domain.FuncionarioId
import br.com.fiap.oficina.funcionario.domain.FuncionarioRepository
import br.com.fiap.oficina.shared.domain.exception.EntityNotFoundException
import org.springframework.stereotype.Service

@Service
internal class RemoverFuncionarioUseCaseImpl(private val repository: FuncionarioRepository) :
    RemoverFuncionarioUseCase {
    override fun executar(id: String) {
        val funcionario =
            repository.buscarPorId(FuncionarioId.toUUID(id))
                ?: throw EntityNotFoundException("Funcionário não encontrado com o ID: $id")

        repository.deletar(funcionario.id)
    }
}
