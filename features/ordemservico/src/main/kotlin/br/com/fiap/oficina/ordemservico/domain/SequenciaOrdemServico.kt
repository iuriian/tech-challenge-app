package br.com.fiap.oficina.ordemservico.domain

/**
 * Fonte do valor sequencial usado na composição do número da ordem de serviço.
 *
 * A formatação é responsabilidade de [NumeroOrdemServico]; aqui obtém-se apenas
 * um valor único e crescente.
 */
interface SequenciaOrdemServico {
    fun proximoValor(): Long
}
