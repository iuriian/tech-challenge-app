package br.com.fiap.oficina.funcionario.application.mapper

import br.com.fiap.oficina.funcionario.application.dto.FuncionarioRequest
import br.com.fiap.oficina.funcionario.application.dto.FuncionarioResponse
import br.com.fiap.oficina.funcionario.domain.Funcionario
import br.com.fiap.oficina.shared.domain.mapper.ApplicationMapper
import br.com.fiap.oficina.shared.domain.valueobject.CPF
import org.springframework.stereotype.Component

@Component
internal class FuncionarioMapper : ApplicationMapper<Funcionario, FuncionarioRequest, FuncionarioResponse> {
    override fun toDomain(request: FuncionarioRequest): Funcionario =
        Funcionario.criar(
            nome = request.nome,
            cpf = CPF.from(request.cpf),
            cargo = request.cargo,
        )

    override fun toResponse(domain: Funcionario): FuncionarioResponse =
        FuncionarioResponse(
            id = domain.id.toString(),
            nome = domain.nome,
            cargoDescricao = domain.cargo.descricao,
            cpf = domain.cpf.valor,
        )
}
