package br.com.fiap.oficina.servico.domain

/**
 * Contrato a ser implementado na camada de infraestrutura para persistência de dados.
 */
internal interface ServicoRepository {
    fun salvar(servico: Servico): Servico

    fun buscarPorId(id: ServicoId): Servico?

    fun listarAtivos(): List<Servico>

    fun listarTodos(): List<Servico>
}
