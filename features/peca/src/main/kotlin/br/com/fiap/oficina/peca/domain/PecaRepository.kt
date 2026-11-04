package br.com.fiap.oficina.peca.domain

/**
 * Contrato a ser implementado na camada de infraestrutura para persistência de dados.
 */
internal interface PecaRepository {
    fun salvar(peca: Peca): Peca

    fun buscarPorCodigo(codigo: String): Peca?

    fun buscarPorId(id: PecaId): Peca?

    fun listar(): List<Peca>
}
