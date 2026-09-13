package br.com.fiap.oficina.funcionario.application.mapper

import br.com.fiap.oficina.funcionario.application.dto.FuncionarioRequest
import br.com.fiap.oficina.funcionario.application.dto.FuncionarioResponse
import br.com.fiap.oficina.funcionario.domain.Funcionario
import org.springframework.stereotype.Component

@Component
class FuncionarioMapper {
    fun toDomain(request: FuncionarioRequest): Funcionario =
        if (request.id == null) {
            Funcionario.criar(
                nome = request.nome,
                cargo = request.cargo,
                cpf = request.cpf,
            )
        } else {
            Funcionario.reconstruir(
                id = request.id,
                nome = request.nome,
                cargo = request.cargo,
                cpf = request.cpf,
            )
        }

    fun toResponse(funcionario: Funcionario): FuncionarioResponse =
        FuncionarioResponse(
            id = funcionario.id.toString(),
            nome = funcionario.nome,
            cargoDescricao = funcionario.cargo.descricao,
            cpf = funcionario.cpf.value,
        )
}
