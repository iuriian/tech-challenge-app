package br.com.fiap.oficina.funcionario.application.usecase

import br.com.fiap.oficina.funcionario.application.dto.FuncionarioRequest
import br.com.fiap.oficina.funcionario.application.dto.FuncionarioResponse
import br.com.fiap.oficina.funcionario.application.mapper.FuncionarioMapper
import br.com.fiap.oficina.funcionario.domain.Funcionario
import br.com.fiap.oficina.funcionario.domain.FuncionarioId
import br.com.fiap.oficina.funcionario.domain.FuncionarioRepository
import br.com.fiap.oficina.shared.domain.exception.BusinessRuleException
import br.com.fiap.oficina.shared.domain.exception.EntityNotFoundException
import br.com.fiap.oficina.shared.domain.valueobject.CPF
import org.springframework.stereotype.Service

@Service
internal class AtualizarFuncionarioUseCaseImpl(
    private val repository: FuncionarioRepository,
    private val mapper: FuncionarioMapper,
) : AtualizarFuncionarioUseCase {
    override fun executar(id: String, request: FuncionarioRequest): FuncionarioResponse {
        require(id.isNotBlank()) { "Id é obrigatório!" }

        val funcionario =
            repository.buscarPorId(FuncionarioId.toUUID(id))
                ?: throw EntityNotFoundException("Funcionário não encontrado!")

        val cpf = CPF.from(request.cpf)

        validarCpfDisponivel(cpf, funcionario)

        val atualizado =
            funcionario.atualizar(
                nome = request.nome,
                cpf = cpf,
                cargo = request.cargo,
            )

        return mapper.toResponse(repository.salvar(atualizado))
    }

    private fun validarCpfDisponivel(cpf: CPF, funcionario: Funcionario) {
        if (cpf == funcionario.cpf) return

        val cpfCadastrado = repository.buscarPorCpf(cpf)

        if (cpfCadastrado != null && cpfCadastrado.id != funcionario.id) {
            throw BusinessRuleException("CPF já cadastrado!")
        }
    }
}
