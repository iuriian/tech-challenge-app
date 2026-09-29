package br.com.fiap.oficina.peca.domain

interface PecaRepository {
    fun salvar(peca: Peca): Peca

    fun buscarPorCodigo(codigo: String): Peca?

    fun buscarPorId(id: PecaId): Peca?

    fun listar(): List<Peca>
}
