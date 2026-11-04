package br.com.fiap.oficina.servico.domain

import java.math.BigDecimal

@ConsistentCopyVisibility
data class Servico private constructor(
    val id: ServicoId,
    val descricao: String,
    val valor: BigDecimal,
    val ativo: Boolean,
) {
    init {
        require(descricao.isNotBlank()) {
            "Descrição do serviço é obrigatória"
        }

        require(valor >= BigDecimal.ZERO) {
            "Valor do serviço não pode ser negativo"
        }
    }

    companion object {
        fun criar(descricao: String, valor: BigDecimal): Servico = Servico(
            id = ServicoId.generate(),
            descricao = descricao,
            valor = valor,
            ativo = true,
        )

        fun reconstruir(id: String, descricao: String, valor: BigDecimal, ativo: Boolean): Servico = Servico(
            id = ServicoId.toUUID(id),
            descricao = descricao,
            valor = valor,
            ativo = ativo,
        )
    }

    fun alterarDescricao(novaDescricao: String): Servico = this.copy(descricao = novaDescricao)

    fun alterarValor(novoValor: BigDecimal): Servico = this.copy(valor = novoValor)

    fun desativar(): Servico = this.copy(ativo = false)

    fun reativar(): Servico = this.copy(ativo = true)
}
