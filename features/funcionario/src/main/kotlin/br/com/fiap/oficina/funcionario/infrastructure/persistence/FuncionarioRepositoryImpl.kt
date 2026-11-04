package br.com.fiap.oficina.funcionario.infrastructure.persistence

import br.com.fiap.oficina.funcionario.domain.Funcionario
import br.com.fiap.oficina.funcionario.domain.FuncionarioId
import br.com.fiap.oficina.funcionario.domain.FuncionarioRepository
import br.com.fiap.oficina.funcionario.infrastructure.mapper.FuncionarioJPAMapper
import br.com.fiap.oficina.shared.domain.valueobject.CPF

internal class FuncionarioRepositoryImpl(
    private val repository: FuncionarioJPARepository,
    private val mapper: FuncionarioJPAMapper,
) : FuncionarioRepository {
    override fun salvar(funcionario: Funcionario): Funcionario {
        val resultado = repository.save(mapper.toJPA(funcionario))
        return mapper.toDomain(resultado)
    }

    override fun listarTodos(): List<Funcionario> = repository.findAll().map(mapper::toDomain)

    override fun buscarPorId(id: FuncionarioId): Funcionario? =
        repository.findById(id.value).map(mapper::toDomain).orElse(null)

    override fun buscarPorCpf(cpf: CPF): Funcionario? = repository.findByCpf(cpf.valor)?.let(mapper::toDomain)

    override fun buscarPorNome(nome: String): Funcionario? = repository.findByNome(nome)?.let(mapper::toDomain)

    override fun deletar(id: FuncionarioId) {
        repository.deleteById(id.value)
    }
}
