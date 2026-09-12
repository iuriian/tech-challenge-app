package br.com.fiap.oficina.funcionario.application.usecase

import br.com.fiap.oficina.funcionario.domain.FuncionarioId
import br.com.fiap.oficina.funcionario.domain.FuncionarioRepository
import org.springframework.stereotype.Service

@Service
internal class RemoverFuncionarioUseCaseImpl(
    private val repository: FuncionarioRepository,
) : RemoverFuncionarioUseCase {
    override fun executar(id: String) {
        repository.deletar(FuncionarioId.toUUID(id))
    }
}
