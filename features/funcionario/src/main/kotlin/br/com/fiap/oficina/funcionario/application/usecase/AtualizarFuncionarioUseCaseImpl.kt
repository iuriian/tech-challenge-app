package br.com.fiap.oficina.funcionario.application.usecase

import br.com.fiap.oficina.funcionario.application.dto.FuncionarioRequest
import br.com.fiap.oficina.funcionario.application.dto.FuncionarioResponse
import br.com.fiap.oficina.funcionario.application.mapper.FuncionarioMapper
import br.com.fiap.oficina.funcionario.domain.Funcionario
import br.com.fiap.oficina.funcionario.domain.FuncionarioException
import br.com.fiap.oficina.funcionario.domain.FuncionarioId
import br.com.fiap.oficina.funcionario.domain.FuncionarioRepository
import org.springframework.stereotype.Service

@Service
internal class AtualizarFuncionarioUseCaseImpl(
    private val repository: FuncionarioRepository,
    private val mapper: FuncionarioMapper,
) : AtualizarFuncionarioUseCase {
    override fun executar(request: FuncionarioRequest): FuncionarioResponse {
        val id = request.id ?: throw FuncionarioException("Id é obrigatório!")

        val funcionario =
            repository.buscarPorId(FuncionarioId.toUUID(id))
                ?: throw FuncionarioException("Funcionário não encontrado!")

        validarCpfDisponivel(request.cpf, funcionario)

        val atualizado =
            funcionario.atualizar(
                nome = request.nome,
                cpf = request.cpf,
                cargo = request.cargo,
            )

        return mapper.toResponse(repository.salvar(atualizado))
    }

    private fun validarCpfDisponivel(
        cpf: String,
        funcionario: Funcionario,
    ) {
        if (cpf == funcionario.cpf.value) return

        val cpfCadastrado = repository.buscarPorCpf(cpf)

        if (cpfCadastrado != null && cpfCadastrado.id != funcionario.id) {
            throw FuncionarioException("CPF já cadastrado!")
        }
    }
}
