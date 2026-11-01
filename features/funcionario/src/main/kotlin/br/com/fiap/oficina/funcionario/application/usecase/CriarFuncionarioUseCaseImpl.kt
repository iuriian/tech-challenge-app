package br.com.fiap.oficina.funcionario.application.usecase

import br.com.fiap.oficina.funcionario.application.dto.FuncionarioRequest
import br.com.fiap.oficina.funcionario.application.dto.FuncionarioResponse
import br.com.fiap.oficina.funcionario.application.mapper.FuncionarioMapper
import br.com.fiap.oficina.funcionario.domain.FuncionarioRepository
import br.com.fiap.oficina.shared.domain.exception.BusinessRuleException
import org.springframework.stereotype.Service

@Service
internal class CriarFuncionarioUseCaseImpl(
    private val repository: FuncionarioRepository,
    private val mapper: FuncionarioMapper,
) : CriarFuncionarioUseCase {
    override fun executar(request: FuncionarioRequest): FuncionarioResponse {
        val funcionario = mapper.toDomain(request)

        if (repository.buscarPorCpf(funcionario.cpf) != null) {
            throw BusinessRuleException("Funcionário já cadastrado")
        }

        return mapper.toResponse(repository.salvar(funcionario))
    }
}
