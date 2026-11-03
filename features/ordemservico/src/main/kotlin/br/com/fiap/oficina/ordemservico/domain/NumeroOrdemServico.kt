package br.com.fiap.oficina.ordemservico.domain

import java.time.Year

data class NumeroOrdemServico(val valor: String) {
    init {
        require(valor.isNotBlank()) {
            "Número da ordem de serviço é obrigatório"
        }
        require(valor.length <= TAMANHO_MAXIMO) {
            "Número da ordem de serviço deve possuir no máximo $TAMANHO_MAXIMO caracteres"
        }
    }

    override fun toString(): String = valor

    companion object {
        private const val TAMANHO_MAXIMO = 50
        private const val TAMANHO_SEQUENCIAL = 6

        fun criar(sequencial: Long, ano: Int = Year.now().value): NumeroOrdemServico {
            require(sequencial > 0) {
                "Sequencial da ordem de serviço deve ser positivo"
            }

            return NumeroOrdemServico(
                valor = "OS-$ano-${sequencial.toString().padStart(TAMANHO_SEQUENCIAL, '0')}",
            )
        }
    }
}
