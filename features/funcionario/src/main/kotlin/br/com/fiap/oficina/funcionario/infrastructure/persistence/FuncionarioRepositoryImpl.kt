package br.com.fiap.oficina.funcionario.infrastructure.persistence

import br.com.fiap.oficina.funcionario.domain.Funcionario
import br.com.fiap.oficina.funcionario.domain.FuncionarioId
import br.com.fiap.oficina.funcionario.domain.FuncionarioRepository
import br.com.fiap.oficina.funcionario.infrastructure.mapper.FuncionarioJPAMapper

internal class FuncionarioRepositoryImpl(
    private val repository: FuncionarioJPARepository,
    private val mapper: FuncionarioJPAMapper,
) : FuncionarioRepository {
    override fun salvar(funcionario: Funcionario): Funcionario {
        val resultado = repository.save(mapper.toJPA(funcionario))
        return mapper.toDomain(resultado)
    }

    override fun listarTodos(): List<Funcionario> = repository.findAll().map { mapper.toDomain(it) }

    override fun buscarPorId(id: FuncionarioId): Funcionario? =
        repository.findById(id.value).map { mapper::toDomain }.orElse(null)

    override fun buscarPorCpf(cpf: String): Funcionario? {
        val resultado = repository.findByCpf(cpf)

        if (resultado != null) {
            return mapper.toDomain(resultado)
        }
        return null
    }

    override fun buscarPorNome(nome: String): Funcionario? {
        val resultado = repository.findByNome(nome)

        if (resultado != null) {
            return mapper.toDomain(resultado)
        }
        return null
    }

    override fun deletar(id: FuncionarioId) {
        repository.deleteById(id.value)
    }
}
