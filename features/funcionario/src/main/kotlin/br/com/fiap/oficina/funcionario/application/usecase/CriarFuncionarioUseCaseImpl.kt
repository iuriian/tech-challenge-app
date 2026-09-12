package br.com.fiap.oficina.funcionario.application.usecase

import br.com.fiap.oficina.funcionario.application.dto.FuncionarioRequest
import br.com.fiap.oficina.funcionario.application.dto.FuncionarioResponse
import br.com.fiap.oficina.funcionario.application.mapper.FuncionarioMapper
import br.com.fiap.oficina.funcionario.domain.FuncionarioException
import br.com.fiap.oficina.funcionario.domain.FuncionarioRepository
import org.springframework.stereotype.Service

@Service
internal class CriarFuncionarioUseCaseImpl(
    private val repository: FuncionarioRepository,
    private val mapper: FuncionarioMapper,
) : CriarFuncionarioUseCase {
    override fun executar(request: FuncionarioRequest): Result<FuncionarioResponse> =
        runCatching {
            if (repository.buscarPorCpf(request.cpf) != null) {
                throw FuncionarioException("Funcionário já cadastrado")
            } else {
                mapper.toDomain(request)
            }
        }.mapCatching { funcionario ->
            repository.salvar(funcionario)
        }.mapCatching {
            mapper.toResponse(it)
        }
}
